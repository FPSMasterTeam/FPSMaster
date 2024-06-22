package top.fpsmaster.modules.ornaments

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import top.fpsmaster.utils.awt.GifUtil
import top.fpsmaster.utils.awt.GifUtil.convertGifToPng
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.utils.os.HttpRequest
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CapeUtils {
    fun downloadCape(name: String, url: String) {
        val textureLocation = ResourceLocation("fpsmaster/capes/$name")
        val file = File(FileUtils.omaments, FileUtils.fixName("$name.png"))
        if (!file.exists() && file.length() < 50) HttpRequest.downloadFile(url, file.absolutePath)
        val textureManager = Minecraft.getMinecraft().textureManager
        val textureArt = ThreadDownloadImageData(file, null, null, null)
        textureManager.loadTexture(textureLocation, textureArt)
    }

    fun downloadCapeGif(name: String, url: String): MutableList<GifUtil.FrameData>? {
        val file = File(FileUtils.omaments, FileUtils.fixName("$name.gif"))
        if (!file.exists()) HttpRequest.downloadFile(url, file.absolutePath)
        val convertGifToPng = convertGifToPng(file.absolutePath)
        val textureManager = Minecraft.getMinecraft().textureManager

        for ((num, frameData) in convertGifToPng.withIndex()) {
            val textureLocation = ResourceLocation("fpsmaster/capes/${name}_$num")
            val file1 = File(FileUtils.omaments, FileUtils.fixName("${name}_$num.png"))
            if (!file1.exists()) {
                // save buffer to file I don't have implemented it! please do it here
                val image: BufferedImage = frameData.image
                ImageIO.write(image, "png", file1)
            }
            val textureArt = ThreadDownloadImageData(file1, null, null, null)
            textureManager.loadTexture(textureLocation, textureArt)
        }
        return convertGifToPng
    }
}
