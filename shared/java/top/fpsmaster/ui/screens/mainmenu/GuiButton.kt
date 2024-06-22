package top.fpsmaster.ui.screens.mainmenu

import net.minecraft.util.ResourceLocation
import top.fpsmaster.FPSMaster
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

class GuiButton(var text: String, var runnable: Runnable) {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var alpha: Double = 100.0

    fun render(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        alpha = if (Render2DUtils.isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt())) {
            base(alpha, 200.0, 0.1)
        } else {
            base(alpha, 100.0, 0.1)
        }
        Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, Color(0, 0, 0, Render2DUtils.limit(alpha)).rgb)
        if (text != "settings") {
            FPSMaster.fontManager.s18.drawCenteredString(
                FPSMaster.i18n[text],
                x + width / 2,
                y + height / 2 - 6,
                FPSMaster.theme.buttonText.rgb
            )
        } else {
            Render2DUtils.drawImage(
                ResourceLocation("client/gui/screen/settings.png"),
                x + width / 2 - 6,
                y + height / 2 - 6,
                12f,
                12f,
                FPSMaster.theme.textColorTitle.rgb
            )
        }
    }

    fun mouseClick(mouseX: Float, mouseY: Float, btn: Int) {
        if (Render2DUtils.isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt()) && btn == 0) {
            runnable.run()
        }
    }
}
