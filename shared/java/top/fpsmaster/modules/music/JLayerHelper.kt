package top.fpsmaster.modules.music

import javazoom.jl.converter.Converter
import javazoom.jl.decoder.JavaLayerException
import java.io.File
import java.io.IOException
import javax.sound.sampled.*

object JLayerHelper {
    var clip: Clip? = null

    val progress: Float
        get() {
            val timeElapsed = clip!!.microsecondPosition
            val total = clip!!.microsecondLength
            return timeElapsed.toFloat() / total
        }

    fun playWAV(wavFile: String) {
        // Open an audio input stream.
        val soundFile = File(wavFile) //you could also get the sound file with an URL
        val audioIn = AudioSystem.getAudioInputStream(soundFile)
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
