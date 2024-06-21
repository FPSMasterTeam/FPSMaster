package top.fpsmaster.ui.common

import top.fpsmaster.FPSMaster
import top.fpsmaster.utils.Utility.isHovered
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

open class GuiButton(var text: String, var runnable: Runnable) {
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f
    private var btnColor = ColorAnimation(Color(113, 127, 254))
    open fun render(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        if (isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt())) {
            btnColor.base(Color(135, 147, 255))
        } else {
            btnColor.base(Color(113, 127, 254))
        }
        Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, btnColor.color)
        FPSMaster.fontManager.s18.drawCenteredString(
            FPSMaster.i18n[text],
            x + width / 2,
            y + height / 2 - 4,
            FPSMaster.theme.getButtonText().rgb
        )
    }

    open fun mouseClick(mouseX: Float, mouseY: Float, btn: Int) {
        if (isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt()) && btn == 0) {
            runnable.run()
        }
    }
}
