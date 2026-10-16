package top.fpsmaster.modules.music;

import net.minecraft.client.Minecraft;
import top.fpsmaster.modules.logger.ClientLogger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 纯 Java 音频播放引擎，基于 {@code javax.sound.sampled} + mp3spi（JLayer）。
 *
 * <p>兼容任意 Java 8 运行时、零原生依赖。支持 播放 / 暂停 / 停止 / 跳转(seek) / 音量 / 进度。
 * 所有 mp3 流先下载到临时文件再解码播放，以便可靠地 seek（重开流 + 丢弃到目标位置）。
 *
 * <p>解码与播放在后台线程进行，绝不阻塞渲染线程；曲目自然播完时通过
 * {@link Minecraft#addScheduledTask(Runnable)} 在主线程回调 {@code onEnded}。
 *
 * <p>每次播放是一个独立的 {@link Session}：线程只认自己的会话，只有当前会话能改进度、时长和音量控件。
 * 以前所有播放共用一个 {@code stopped} 标志，旧线程卡在慢速下载里时，新曲目把标志改回 false，
 * 旧线程醒来就接着播，两首歌叠在一起；停止也不再在主线程 join，而是断开旧连接让阻塞的读立即失败。
 */
public class AudioEngine {

    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/123.0.0.0 Safari/537.36";

    /** One playback: its thread, connection and controls. Cancelled sessions only clean up after themselves. */
    private static final class Session {
        volatile boolean cancelled;
        /** Still downloading: nothing audible yet. */
        volatile boolean buffering;
        volatile HttpURLConnection connection;
        volatile long pendingSeekMs = -1;
        volatile FloatControl gainControl;
        Thread thread;

        void cancel() {
            cancelled = true;
            HttpURLConnection conn = connection;
            if (conn != null) {
                // Socket reads ignore interrupts; disconnecting makes a read blocked on a slow CDN
                // fail now instead of after the 20 s read timeout.
                conn.disconnect();
            }
            Thread t = thread;
            if (t != null) {
                t.interrupt();
            }
        }
    }

    private volatile Session current;
    private volatile boolean paused = false;

    private volatile long positionMs = 0;
    private volatile long durationMs = 0;
    private volatile float volume = 0.7f; // 0..1

    /** 是否有曲目（含暂停与缓冲中）。 */
    public boolean isActive() {
        return current != null;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isPlaying() {
        Session session = current;
        return session != null && !session.buffering && !paused;
    }

    /** 正在下载、还没开始出声。 */
    public boolean isBuffering() {
        Session session = current;
        return session != null && session.buffering;
    }

    public long getPositionMs() {
        return positionMs;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public float getVolume() {
        return volume;
    }

    /**
     * 播放一个音频直链。会先停止当前曲目，然后异步下载 + 解码播放。
     *
     * @param url         音频直链（建议为 mp3）。
     * @param referer     可选 Referer（QQ 直链有时需要 https://y.qq.com/）；可为 null。
     * @param knownDurationMs 已知时长（毫秒），用于进度条；未知传 0。
     * @param onEnded     曲目自然播完时在主线程回调；可为 null。
     */
    public void play(final String url, final String referer, long knownDurationMs, final Runnable onEnded) {
        final Session session = begin(knownDurationMs);
        session.buffering = true;
        start(session, new Runnable() {
            @Override
            public void run() {
                File temp = null;
                try {
                    temp = downloadToTemp(session, url, referer);
                    if (temp == null) {
                        return;
                    }
                    session.buffering = false;
                    playLoop(session, temp, onEnded);
                } catch (Throwable e) {
                    if (!session.cancelled) {
                        ClientLogger.error("Music playback failed: " + e.getMessage());
                        finish(session);
                    }
                } finally {
                    session.buffering = false;
                    if (temp != null) {
                        //noinspection ResultOfMethodCallIgnored
                        temp.delete();
                    }
                }
            }
        }, "FPSMaster-Music-Audio");
    }

    public void playFile(final File file, long knownDurationMs, final Runnable onEnded) {
        final Session session = begin(knownDurationMs);
        start(session, new Runnable() {
            @Override
            public void run() {
                try {
                    playLoop(session, file, onEnded);
                } catch (Throwable e) {
                    if (!session.cancelled) {
                        ClientLogger.error("Local music playback failed: " + e.getMessage());
                        finish(session);
                    }
                }
            }
        }, "FPSMaster-Music-Local");
    }

    private Session begin(long knownDurationMs) {
        stop();
        Session session = new Session();
        paused = false;
        positionMs = 0;
        durationMs = knownDurationMs;
        current = session;
        return session;
    }

    private void start(Session session, Runnable body, String name) {
        Thread t = new Thread(body, name);
        t.setDaemon(true);
        session.thread = t;
        t.start();
    }

    /** The session ended on its own (finished or failed): it is no longer the current one. */
    private void finish(Session session) {
        if (current == session) {
            current = null;
            paused = false;
        }
    }

    private boolean live(Session session) {
        return !session.cancelled && current == session;
    }

    private void playLoop(Session session, File file, Runnable onEnded) throws Exception {
        long startMs = 0;
        boolean endedNaturally = false;

        while (live(session)) {
            AudioInputStream fileIn = null;
            AudioInputStream din = null;
            SourceDataLine line = null;
            boolean seekBreak = false;
            try {
                fileIn = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
                AudioFormat base = fileIn.getFormat();
                if (durationMs <= 0 && fileIn.getFrameLength() > 0 && base.getFrameRate() > 0) {
                    durationMs = (long) (fileIn.getFrameLength() * 1000.0 / base.getFrameRate());
                }
                AudioFormat decoded = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(), 16, base.getChannels(),
                        base.getChannels() * 2, base.getSampleRate(), false);
                din = AudioSystem.getAudioInputStream(decoded, fileIn);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decoded);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(decoded);
                line.start();
                applyGain(session, line);

                double bytesPerMs = decoded.getFrameRate() * decoded.getFrameSize() / 1000.0;
                int frameSize = Math.max(1, decoded.getFrameSize());

                if (startMs > 0) {
                    // PCM has to be skipped a whole frame at a time — one frame is channels × 2 bytes
                    // for 16-bit audio. Landing mid-frame shifts every following sample by a byte or
                    // two, so the high/low halves of each sample and the left/right channels are read
                    // out of position and the result is white noise. startMs * bytesPerMs is only a
                    // multiple of the frame size by luck, which is why it happened on some seeks and
                    // not others, and why seeking again "fixed" it.
                    long skipBytes = (long) (startMs * bytesPerMs);
                    skipBytes -= skipBytes % frameSize;
                    skipDecoded(session, din, skipBytes);
                }
                long posBytes = 0;
                byte[] buf = new byte[4096];
                // Bytes left over from the previous read that do not yet complete a frame.
                int carry = 0;

                while (live(session)) {
                    long seek = session.pendingSeekMs;
                    if (seek >= 0) {
                        session.pendingSeekMs = -1;
                        startMs = seek;
                        seekBreak = true;
                        // Discard whatever is still queued in the line before tearing it down. The
                        // buffer holds PCM decoded at the *old* position; closing without flushing
                        // lets it play out as a burst of noise just before the new stream starts.
                        // stop() first so flush() is not racing an active playback pointer.
                        line.stop();
                        line.flush();
                        break;
                    }
                    if (paused) {
                        Thread.sleep(30);
                        continue;
                    }
                    int n = din.read(buf, carry, buf.length - carry);
                    if (n < 0) {
                        endedNaturally = true;
                        break;
                    }
                    // SourceDataLine.write is documented as undefined unless the length is a whole
                    // number of frames, and the decoder does not promise frame-aligned reads. Write
                    // only the complete frames and carry the remainder into the next read.
                    int available = carry + n;
                    int writable = available - (available % frameSize);
                    if (writable > 0) {
                        line.write(buf, 0, writable);
                        posBytes += writable;
                        if (current == session) {
                            positionMs = startMs + (long) (posBytes / bytesPerMs);
                        }
                    }
                    carry = available - writable;
                    if (carry > 0) {
                        System.arraycopy(buf, writable, buf, 0, carry);
                    }
                }
            } catch (InterruptedException cancelledWhilePaused) {
                return;
            } finally {
                closeQuietly(line, din, fileIn);
            }

            if (!live(session)) {
                return;
            }
            if (seekBreak) {
                continue; // 重开流并跳到 startMs
            }
            if (endedNaturally) {
                break;
            }
        }

        if (endedNaturally && live(session)) {
            finish(session);
            Minecraft mc = Minecraft.getMinecraft();
            if (onEnded != null && mc != null) {
                mc.addScheduledTask(onEnded);
            }
        }
    }

    /** 逐块读丢弃到目标字节数（mp3spi 的 skip 不可靠，故用读丢弃）。 */
    private void skipDecoded(Session session, AudioInputStream din, long bytes) throws Exception {
        byte[] junk = new byte[4096];
        long remaining = bytes;
        while (remaining > 0 && live(session)) {
            int toRead = (int) Math.min(junk.length, remaining);
            int n = din.read(junk, 0, toRead);
            if (n < 0) break;
            remaining -= n;
        }
    }

    private void applyGain(Session session, SourceDataLine line) {
        try {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl ctl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                session.gainControl = ctl;
                setGainDb(ctl, volume);
            } else {
                session.gainControl = null;
            }
        } catch (Exception unsupported) {
            // Some mixers advertise MASTER_GAIN and then refuse it; playback still works at line level.
            session.gainControl = null;
        }
    }

    private void setGainDb(FloatControl ctl, float vol) {
        float min = ctl.getMinimum();
        float max = ctl.getMaximum();
        float db;
        if (vol <= 0.0001f) {
            db = min;
        } else {
            db = (float) (20.0 * Math.log10(vol));
        }
        if (db < min) db = min;
        if (db > max) db = max;
        ctl.setValue(db);
    }

    /** 设置音量 0..1，立即生效。 */
    public void setVolume(float vol) {
        if (vol < 0) vol = 0;
        if (vol > 1) vol = 1;
        this.volume = vol;
        Session session = current;
        FloatControl ctl = session == null ? null : session.gainControl;
        if (ctl != null) {
            try {
                setGainDb(ctl, vol);
            } catch (Exception lineClosed) {
                // The line closed between the read and the call; the next line picks the volume up.
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    /** 跳转到指定毫秒。 */
    public void seek(long ms) {
        Session session = current;
        if (session == null) {
            return;
        }
        if (ms < 0) ms = 0;
        if (durationMs > 0 && ms > durationMs) ms = durationMs;
        positionMs = ms;
        session.pendingSeekMs = ms;
        paused = false;
    }

    /**
     * 停止当前曲目。不等待旧线程：它在断开的连接或下一次循环检查时自己退出并清理，
     * 主线程不会再因此卡住。
     */
    public void stop() {
        Session session = current;
        current = null;
        paused = false;
        if (session != null) {
            session.cancel();
        }
        positionMs = 0;
    }

    /** 下载到临时文件；会话被取消时返回 null。 */
    private File downloadToTemp(Session session, String url, String referer) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        session.connection = conn;
        if (session.cancelled) {
            conn.disconnect();
            return null;
        }
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(20_000);
        conn.setRequestProperty("User-Agent", UA);
        if (referer != null) {
            conn.setRequestProperty("Referer", referer);
        }
        File temp = File.createTempFile("fpsmaster-music-", ".mp3");
        temp.deleteOnExit();
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = conn.getInputStream();
            out = new FileOutputStream(temp);
            byte[] buf = new byte[16384];
            int n;
            while ((n = in.read(buf)) != -1) {
                if (session.cancelled) {
                    break;
                }
                out.write(buf, 0, n);
            }
        } catch (java.io.IOException failure) {
            closeQuietly(out);
            out = null;
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
            if (session.cancelled) {
                return null;
            }
            throw failure;
        } finally {
            closeQuietly(in);
            closeQuietly(out);
            conn.disconnect();
            session.connection = null;
        }
        if (session.cancelled) {
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
            return null;
        }
        return temp;
    }

    private static void closeQuietly(SourceDataLine line, AudioInputStream din, AudioInputStream fileIn) {
        if (line != null) {
            try {
                line.stop();
                line.flush();
                line.close();
            } catch (Exception alreadyClosed) {
                // Teardown of a line the mixer already dropped; nothing left to release.
            }
        }
        closeQuietly(din);
        closeQuietly(fileIn);
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception alreadyClosed) {
                // Closing is best effort during teardown.
            }
        }
    }
}
