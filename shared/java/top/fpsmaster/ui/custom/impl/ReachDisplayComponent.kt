package top.fpsmaster.ui.custom.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.ReachDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.utils.shader.BlurBuffer

class ReachDisplayComponent : Component(ReachDisplay::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        val s = "${ReachDisplay.reach} b"
        val s18 = FPSMaster.INSTANCE.fontManager!!.s18
        width = getStringWidth(s18, s) + 4
        height = 14f
        drawRect(x - 2, y, width, height, mod.backgroundColor.color)
        drawString(s18, s, x, y + 2, ReachDisplay.textColor.rGB)
    }
}
