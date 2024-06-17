package top.fpsmaster.ui.click.ornaments

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.modules.logger.Logger.error
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.utils.os.FileUtils.fixName
import top.fpsmaster.utils.os.HttpRequest.downloadFile
import top.fpsmaster.wrapper.WorldClientProvider
import java.io.File

class Item(var category: String, var itemId: String, var name: String, var price: String, private var img: String) {
    var imgLoaded = false
    var x = 0f
    var y = 0f

    init {
        loadImg()
    }

    private fun loadImg() {
        Thread(Runnable {
            try {
                if (ProviderManager.worldClientProvider.getWorld() == null) return@Runnable
                val artist = File(
                    FileUtils.omaments, fixName(
                        name + "_preview.png"
                    )
                )
                if (!artist.exists()) downloadFile(img, artist.absolutePath)
                val textureManager = Minecraft.getMinecraft().textureManager
                val textureLocation = ResourceLocation("ornaments/" + name + "_preview")
                val textureArt = ThreadDownloadImageData(artist, null, null, null)
                textureManager.loadTexture(textureLocation, textureArt)
                imgLoaded = true
            } catch (e: Exception) {
                error("ornament", "Load failed $name")
            }
        }).start()
    }
}
