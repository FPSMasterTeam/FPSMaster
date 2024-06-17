package top.fpsmaster.ui.custom.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.CoordsDisplay
import top.fpsmaster.features.impl.interfaces.FPSDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.TextFormattingProvider

class CoordsDisplayComponent : Component(CoordsDisplay::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        val s18 = FPSMaster.INSTANCE.fontManager!!.s18

        var s =
            "X:${ProviderManager.mcProvider.getPlayer()!!.posX.toInt()} Y:${ProviderManager.mcProvider.getPlayer()!!.posY.toInt()} Z:${ProviderManager.mcProvider.getPlayer()!!.posZ.toInt()}"
        if ((mod as CoordsDisplay).limitDisplay.value) {
            val restHeight =
                (mod as CoordsDisplay).limitDisplayY.value.toInt() - ProviderManager.mcProvider.getPlayer()!!.posY.toInt()
            var yStr = ""
            // color
            if (restHeight < 5) {
                yStr = "${TextFormattingProvider.getRed()}$restHeight${TextFormattingProvider.getReset()}"
            } else if (restHeight < 10) {
                yStr = "${TextFormattingProvider.getYellow()}$restHeight${TextFormattingProvider.getReset()}"
            } else {
                yStr = "${TextFormattingProvider.getGreen()}$restHeight${TextFormattingProvider.getReset()}"
            }

            s =
                "X:${ProviderManager.mcProvider.getPlayer()!!.posX.toInt()} Y:${ProviderManager.mcProvider.getPlayer()!!.posY.toInt()}($yStr) Z:${ProviderManager.mcProvider.getPlayer()!!.posZ.toInt()}"
        }
        width = getStringWidth(s18, s) + 4
        height = 14f
        drawRect(x - 2, y, width, height, mod.backgroundColor.color)
        drawString(s18, s, x, y + 2, FPSDisplay.textColor.rGB)
    }
}