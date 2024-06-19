package top.fpsmaster.ui.screens.oobe.impls

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
import top.fpsmaster.FPSMaster
import top.fpsmaster.ui.screens.oobe.Scene
import top.fpsmaster.ui.common.GuiButton
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color
import java.awt.Desktop
import java.net.URI

class First : Scene() {
    var btn: GuiButton = GuiButton(FPSMaster.INSTANCE.i18n["oobe.first.next"]) {
        val url = "https://fpsmaster.top/tutorial"
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(URI(url))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        FPSMaster.INSTANCE.oobeScreen.nextScene()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        val sr = ScaledResolution(Minecraft.getMinecraft())
        Render2DUtils.drawRect(
            0f,
            0f,
            sr.scaledWidth.toFloat(),
            sr.scaledHeight.toFloat(),
            Color(235, 242, 255).rgb
        )
        FPSMaster.INSTANCE.fontManager!!.s36.drawCenteredString(
            FPSMaster.INSTANCE.i18n["oobe.first.desc"],
            sr.scaledWidth / 2f,
            sr.scaledHeight / 2f - 60,
            FPSMaster.theme.getTextColorDescription().rgb
        )
        FPSMaster.INSTANCE.fontManager!!.s40.drawCenteredString(
            FPSMaster.INSTANCE.i18n["oobe.first.title"],
            sr.scaledWidth / 2f,
            sr.scaledHeight / 2f - 40,
            FPSMaster.theme.getPrimary().rgb
        )
        val skip = FPSMaster.INSTANCE.i18n["oobe.first.skip"]
        val skipFont = FPSMaster.INSTANCE.fontManager!!.s22
        val skipWidth = skipFont.getStringWidth(skip)
        val x = sr.scaledWidth - 10 - skipWidth
        val skipY = sr.scaledHeight - 20
        skipFont.drawString(skip, x.toFloat(), skipY, FPSMaster.theme.getTextColorDescription().rgb)
        btn.render(sr.scaledWidth / 2f - 30, sr.scaledHeight / 2f + 40, 60f, 24f, mouseX.toFloat(), mouseY.toFloat())
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClick(mouseX, mouseY, mouseButton)
        val sr = ScaledResolution(Minecraft.getMinecraft())
        btn.mouseClick(mouseX.toFloat(), mouseY.toFloat(), mouseButton)
        val skipFont = FPSMaster.INSTANCE.fontManager!!.s22
        val skip = FPSMaster.INSTANCE.i18n["oobe.first.skip"]
        val skipWidth = skipFont.getStringWidth(skip)
        val x = sr.scaledWidth - 10 - skipWidth
        val skipY = sr.scaledHeight - 20
        if (Render2DUtils.isHovered(
                x.toFloat(),
                skipY.toFloat(),
                skipWidth.toFloat(),
                20f,
                mouseX,
                mouseY
            ) && Mouse.isButtonDown(0)
        ) {
            FPSMaster.INSTANCE.oobeScreen.nextScene()
        }
    }
}
