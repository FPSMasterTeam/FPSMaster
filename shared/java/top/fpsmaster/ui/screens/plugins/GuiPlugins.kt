package top.fpsmaster.ui.screens.plugins

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import org.json.JSONObject
import top.fpsmaster.FPSMaster
import top.fpsmaster.ui.screens.mainmenu.MainMenu
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.utils.os.HttpRequest
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color
import java.awt.Desktop
import java.net.URI

class GuiPlugins : GuiScreen() {
    private var fail: Boolean

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawDefaultBackground()
        val sr = ScaledResolution(mc)
        val width = sr.scaledWidth / 2f
        val height = sr.scaledHeight / 2f
        Render2DUtils.drawOptimizedRoundedRect(width / 2f, height / 2f, width, height, Color(0, 0, 0, 140).rgb)
        FPSMaster.fontManager.s24.drawCenteredString(
            FPSMaster.i18n["plugin_manager.title"],
            width,
            height / 2f - 20,
            Color.WHITE.rgb
        )

        var pluginY = height / 2f
        for (plugin: OnlinePlugin in plugins) {
            FPSMaster.fontManager.s18.drawString(
                "${plugin.name} - ${plugin.author} ${plugin.version}",
                width / 2f + 10,
                pluginY + 4,
                Color.WHITE.rgb
            )
            FPSMaster.fontManager.s16.drawString(
                plugin.desc,
                width / 2f + 10,
                pluginY + 16,
                Color.GRAY.rgb
            )
            if (Render2DUtils.isHovered(width / 2f, pluginY, width, 30f, mouseX, mouseY)) {
                Render2DUtils.drawOptimizedRoundedRect(
                    width / 2f,
                    pluginY,
                    width,
                    30f,
                    Color(255, 255, 255, 80).rgb
                )
                FPSMaster.fontManager.s16.drawString(
                    "安装",
                    width * 1.5f - 24,
                    pluginY + 10,
                    Color.WHITE.rgb
                )
            } else {
                FPSMaster.fontManager.s16.drawString("安装", width * 1.5f - 24, pluginY + 10, Color.GRAY.rgb)
            }
            pluginY += 30
        }

        Render2DUtils.drawOptimizedRoundedRect(width / 2f, height * 1.5f + 10, width, 26f, Color(0, 0, 0, 140).rgb)
        FPSMaster.fontManager.s18.drawCenteredString(
            "打开插件文件夹",
            width,
            height * 1.5f + 18,
            Color.WHITE.rgb
        )

        Render2DUtils.drawOptimizedRoundedRect(width / 2f, height * 1.5f + 40, width, 26f, Color(0, 0, 0, 140).rgb)

        FPSMaster.fontManager.s18.drawCenteredString(
            "重载插件",
            width,
            height * 1.5f + 48,
            Color.WHITE.rgb
        )

        FPSMaster.fontManager.s18.drawString(
            "《FPSMaster 插件服务条款》",
            5f,
            sr.scaledHeight - 18f,
            Color.WHITE.rgb
        )
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        val sr = ScaledResolution(mc)
        val width = sr.scaledWidth / 2f
        val height = sr.scaledHeight / 2f
        var pluginY = height / 2f
        for (plugin: OnlinePlugin in plugins) {
            if (Render2DUtils.isHovered(width / 2f, pluginY, width, 30f, mouseX, mouseY)) {
                HttpRequest.downloadFile(plugin.link, FileUtils.plugins.absolutePath + "/" + plugin.name + ".jar")
                FPSMaster.plugins.reload()
                mc.displayGuiScreen(MainMenu())
            }
            pluginY += 30
        }

        if (Render2DUtils.isHovered(width / 2f, height * 1.5f + 10, width - 10, 26f, mouseX, mouseY)) {
            Desktop.getDesktop().open(FileUtils.plugins)
        }

        if (Render2DUtils.isHovered(0f, sr.scaledHeight- 20f, 100f, 20f, mouseX, mouseY)) {
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(URI("https://fpsmaster.top/plugin-tos"))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        if (Render2DUtils.isHovered(width / 2f, height * 1.5f + 40, width - 10, 26f, mouseX, mouseY)) {
            FPSMaster.plugins.reload()
        }
    }

    private var plugins = ArrayList<OnlinePlugin>()

    init {
        try {
            val list = HttpRequest["${FPSMaster.FILE_API}/plugins/list.json"]
            val json = JSONObject(list)
            json.getJSONArray("plugins").forEach {
                val jsonObject = it as JSONObject
                val name = jsonObject.getString("name")
                val desc = jsonObject.getString("desc")
                val author = jsonObject.getString("author")
                val website = jsonObject.getString("website")
                val version = jsonObject.getString("version")
                val link = jsonObject.getString("link")
                val plugin = OnlinePlugin(name, desc, author, website, version, link)
                plugins.add(plugin)
            }
            fail = false
        } catch (e: Exception) {
            fail = true
            e.printStackTrace()
        }
    }
}