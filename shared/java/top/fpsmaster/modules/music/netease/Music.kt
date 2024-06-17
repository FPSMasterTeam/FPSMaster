package top.fpsmaster.modules.music.netease

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.modules.logger.Logger.error
import top.fpsmaster.modules.logger.Logger.warn
import top.fpsmaster.modules.music.AbstractMusic
import top.fpsmaster.modules.music.MusicPlayer
import top.fpsmaster.modules.music.MusicPlayer.playFile
import top.fpsmaster.modules.music.netease.deserialize.MusicWrapper
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.utils.os.FileUtils.fixName
import top.fpsmaster.utils.os.FileUtils.music
import top.fpsmaster.utils.os.HttpRequest.downloadFile
import top.fpsmaster.wrapper.WorldClientProvider
import java.io.File

class Music(id: Long, name: String?, artists: String?, picUrl: String) : AbstractMusic() {
    var imgURL: String
    var isLoadedImage = false
    var musicURL: String? = null
    @JvmField
    var id: String

    init {
        this.id = id.toString()
        this.name = name
        author = artists
        imgURL = picUrl
        // load music with thread
        Thread { loadMusic() }.start()
    }

    fun loadMusic() {
        try {
            if (ProviderManager.worldClientProvider.getWorld() == null) return
            MusicWrapper.loadLyrics(this)
            val artist = File(
                FileUtils.artists, fixName(
                    "$name($id).png"
                )
            )
            if (!artist.exists()) downloadFile("$imgURL?param=90y90", artist.absolutePath)
            val textureManager = Minecraft.getMinecraft().textureManager
            val textureLocation = ResourceLocation("music/netease/$id")
            val textureArt = ThreadDownloadImageData(artist, null, null, null)
            textureManager.loadTexture(textureLocation, textureArt)
            isLoadedImage = true
        } catch (e: Exception) {
            error("music", "Load failed $name($id)")
        }
    }

    override fun play() {
        val flac: File = File(music, fixName("$name($id).flac"))
        val mp3: File = File(music, fixName("$name($id).mp3"))
        if (flac.exists() || mp3.exists()) {
            Thread {
                if (flac.exists()) {
                    playFile(flac.absolutePath)
                } else {
                    playFile(mp3.absolutePath)
                }
            }.start()
        } else {
            downloadThread = Thread { download() }
            downloadThread!!.start()
        }
        MusicPlayer.startTime = System.currentTimeMillis()
    }

    private fun download() {
        try {
            musicURL = MusicWrapper.getSongUrl(id)
            val download: File = if (musicURL!!.endsWith(".flac")) File(music, fixName("$name($id).flac")) else {
                File(music, fixName("$name($id).mp3"))
            }
            if (!download.exists()) {
                downloadFile(musicURL!!, download.absolutePath)
                play()
            }
        } catch (e: Exception) {
            warn("Download failed $name($id)")
        }
    }

    companion object {
        var downloadThread: Thread? = null
    }
}
