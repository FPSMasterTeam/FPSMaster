package top.fpsmaster.utils.awt

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import top.fpsmaster.utils.os.FileUtils
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object RoundUtil {
    var generated: ArrayList<Int> = ArrayList()

    @JvmStatic
    fun generateRound(radius: Int) {
        if (generated.contains(radius)) {
            return
        }
        val dir = FileUtils.round

        if (!dir.exists()) {
            dir.mkdirs()
        }

        var needGenerate = false
        try {
            val fileNames = arrayOf("lt.png", "rt.png", "lb.png", "rb.png") // 存储文件名
            for (fileName in fileNames) {
                if (!File(dir, fileName).exists()) {
                    needGenerate = true
                    break
                }
            }
            if (!needGenerate) {
                for (fileName in fileNames) {
                    loadToTexture(radius, fileName)
                    generated.add(radius)
                }
                return
            }
            val radius2 = radius * 2

            val bufferedImage = BufferedImage(radius2, radius2, BufferedImage.TYPE_INT_ARGB)
            val graphics2D = bufferedImage.createGraphics()
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            graphics2D.color = Color(0, 0, 0, 0)
            graphics2D.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

            var roundRectangle: RoundRectangle2D
            var outputfile: File


            val coordinates = arrayOf(0, -radius2, 0, -radius2) // 对应每个文件的x坐标
            val coordinates2 = arrayOf(0, 0, -radius2, -radius2) // 对应每个文件的x坐标

            for (i in 0..3) {
                graphics2D.composite = AlphaComposite.Clear
                graphics2D.fillRect(0, 0, radius2, radius2)
                graphics2D.composite = AlphaComposite.SrcOver
                graphics2D.color = Color.WHITE
                roundRectangle = RoundRectangle2D.Float(
                    coordinates[i].toFloat(),
                    coordinates2[i].toFloat(),
                    (radius2 * 2).toFloat(),
                    (radius2 * 2).toFloat(),
                    radius2.toFloat(),
                    radius2.toFloat()
                )
                graphics2D.fill(roundRectangle)
                outputfile = File(dir.absolutePath + "/" + radius.toString() + "_" + fileNames[i])
                ImageIO.write(bufferedImage, "png", outputfile)
                loadToTexture(radius, fileNames[i])
            }

            graphics2D.dispose()
            generated.add(radius)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun loadToTexture(radius: Int, pos: String) {
        val textureManager = Minecraft.getMinecraft().textureManager
        val textureLocation = ResourceLocation("fpsmaster/rounded/$radius/$pos")
        val cacheFileIn = File(FileUtils.round, radius.toString() + "_" + pos)
        val textureArt = ThreadDownloadImageData(cacheFileIn, null, null, null)
        textureManager.loadTexture(textureLocation, textureArt)
    }
}
