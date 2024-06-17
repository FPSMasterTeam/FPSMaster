package top.fpsmaster.ui.custom.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.PingDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.interfaces.ProviderManager

class PingDisplayComponent : Component(PingDisplay::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        val s16 = FPSMaster.INSTANCE.fontManager!!.s16
        // get ping of connection
        if (ProviderManager.mcProvider.getPlayer() == null) return
        val ping = "${ProviderManager.mcProvider.getRespondTime()}ms"
        val text = "Ping: $ping"
        width = getStringWidth(s16, text) + 4
        height = 14f
        drawRect(x - 2, y, width, height, mod.backgroundColor.color)
        drawString(s16, text, x, y + 2, PingDisplay.textColor.rGB)
    }
}