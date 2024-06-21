package top.fpsmaster.ui.screens.oobe.impls

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import top.fpsmaster.FPSMaster
import top.fpsmaster.ui.screens.oobe.Scene
import top.fpsmaster.ui.common.GuiButton
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

class Welcome : Scene() {
    var btn: GuiButton = GuiButton(FPSMaster.i18n["oobe.welcome.next"]) { FPSMaster.oobeScreen.nextScene() }

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
        FPSMaster.fontManager.s40.drawCenteredString(
            FPSMaster.i18n["oobe.welcome.title"],
            sr.scaledWidth / 2f,
            sr.scaledHeight / 2f - 40,
            FPSMaster.theme.getPrimary().rgb
        )
        btn.render(sr.scaledWidth / 2f - 30, sr.scaledHeight / 2f + 40, 60f, 24f, mouseX.toFloat(), mouseY.toFloat())
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClick(mouseX, mouseY, mouseButton)
        btn.mouseClick(mouseX.toFloat(), mouseY.toFloat(), mouseButton)
    }
}
