package top.fpsmaster.modules.music

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import javazoom.jl.converter.Converter
import javazoom.jl.decoder.JavaLayerException
import java.io.File
import java.io.IOException
import javax.sound.sampled.*
import kotlin.math.min
import kotlin.math.sqrt

object JLayerHelper {
    var clip: Clip? = null
    var audIn: AudioInputStream? = null
    var audioBytes: ByteArray = ByteArray(0)
    var loudnessCurve: DoubleArray? = DoubleArray(0)
    val progress: Float
        get() {
            val timeElapsed = clip!!.microsecondPosition
            val total = clip!!.microsecondLength
            return timeElapsed.toFloat() / total
        }

    fun playWAV(wavFile: String) {
        // Open an audio input stream.
        val soundFile = File(wavFile) //you could also get the sound file with an URL
        var audioIn = AudioSystem.getAudioInputStream(soundFile)
        audIn = AudioSystem.getAudioInputStream(soundFile)
        audioBytes = readAudioData(audIn!!)
        // Get a sound clip resource.
        clip = AudioSystem.getClip()
        // Open audio clip and load samples from the audio input stream.
        if (clip == null)
            return
        clip!!.open(audioIn)
        clip!!.start()
    }

    @JvmStatic
    fun seek(progress: Float) {
        val totalTime = clip!!.microsecondLength
        val currentTime = (totalTime * progress).toLong() //将播放进度设置为50%
        clip!!.microsecondPosition = currentTime
    }

    fun updateLoudness() {
        if (clip == null)
            return
        if (audIn == null) {
            return
        }
        val totalTime = clip!!.microsecondLength
        val currentTime = (totalTime * progress).toLong()

        val format = audIn?.format
        if (format!!.getEncoding() != AudioFormat.Encoding.PCM_SIGNED ||
            format.getSampleSizeInBits() != 16
        ) {
            println("Unsupported audio format: $format");
            return
        }

        val targetTime: Float = (currentTime / 1000 / 1000f)

        val segment = getAudioSegment(
            audioBytes,
            format.sampleRate,
            format.frameSize,
            targetTime,
            1f
        )

        // 进行 FFT 分析
        val fftData = performFFT(segment)

        // 计算振幅（响度）
        val amplitudes = computeAmplitude(fftData)

        loudnessCurve = amplitudes
    }

    @Throws(IOException::class)
    fun readAudioData(audioInputStream: AudioInputStream): ByteArray {
        val bufferSize = (audioInputStream.frameLength * audioInputStream.format.frameSize).toInt()
        val audioBytes = ByteArray(bufferSize)
        audioInputStream.read(audioBytes)
        return audioBytes
    }

    fun getAudioSegment(
        audioData: ByteArray,
        sampleRate: Float,
        bytesPerFrame: Int,
        startSecond: Float,
        durationInSeconds: Float
    ): ByteArray {
        val startSample = startSecond * sampleRate.toInt()
        val numSamples = durationInSeconds * sampleRate.toInt()
        val startByte: Int = (startSample * bytesPerFrame).toInt()
        val numBytes: Int = (numSamples * bytesPerFrame).toInt()

        return audioData.copyOfRange(startByte, startByte + numBytes)
    }


    fun performFFT(buffer: ByteArray): DoubleArray {
        val audioData = DoubleArray(buffer.size / 2)
        for (i in audioData.indices) {
            audioData[i] = buffer[i] / 128.0
        }

        // 执行傅里叶变换
        val fft = DoubleFFT_1D(1024)
        fft.realForward(audioData)

        return audioData
    }


    fun computeAmplitude(fftData: DoubleArray): DoubleArray {
        val numFrequencies = fftData.size / 2
        val numBlocks = 300
        val amplitudes = DoubleArray(numFrequencies)


        // 归一化处理
        val min = amplitudes.minOrNull() ?: 0.0
        val max = amplitudes.maxOrNull() ?: 1.0
        val normalizedAmplitudes = amplitudes.map { amplitude ->
            (amplitude - min) / (max - min)
        }.toDoubleArray()

        // 每个块的大小
        val blockSize = amplitudes.size / numBlocks
        val blockAverages = DoubleArray(numBlocks)

        // 分块并计算均值
        for (block in 0 until numBlocks) {
            val startIdx = block * blockSize
            val endIdx = if (block == numBlocks - 1) normalizedAmplitudes.size else startIdx + blockSize
            val blockSum = normalizedAmplitudes.slice(startIdx until endIdx).sum()
            blockAverages[block] = blockSum / (endIdx - startIdx)
        }

        return blockAverages
    }

    fun setVolume(vol: Float) {
        var vol = vol
        vol /= 2
        vol += 0.5f
        val volumeControl = clip!!.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        // Change the volume to half way between minimum and maximum
        val volume = (volumeControl.maximum - volumeControl.minimum) * vol + volumeControl.minimum
        volumeControl.value = volume
    }

    fun convert(sourcePath: String, targetPath: String) {
        try {
            val converter = Converter()
            val sourceFile = File(sourcePath)
            val targetFile = File(targetPath)
            converter.convert(sourceFile.path, targetFile.path)
        } catch (e: JavaLayerException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        clip!!.stop()
    }

    fun start() {
        clip!!.start()
    }

    @JvmStatic
    val duration: Double
        get() = (clip!!.microsecondLength / 1000f / 1000f / 60f).toDouble()
}
