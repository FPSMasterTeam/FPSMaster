package top.fpsmaster.features.settings.impl.utils

import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

class CustomColor {
    var hue: Float = 0f
    var brightness: Float = 0f
    var saturation: Float = 0f
    var alpha: Float = 0f
    var color: Color = Color(255, 255, 255)
        set(color) {
            val col = Color.RGBtoHSB(color.red, color.green, color.blue, floatArrayOf(hue, saturation, brightness))
            hue = col[0]
            saturation = col[1]
            brightness = col[2]
            field = Render2DUtils.reAlpha(
                Color.getHSBColor(hue, saturation, brightness),
                Render2DUtils.limit((alpha * 255).toDouble())
            )
        }

    constructor(hue: Float, brightness: Float, saturation: Float, alpha: Float) {
        this.hue = hue
        this.brightness = brightness
        this.saturation = saturation
        this.alpha = alpha
        color = Render2DUtils.reAlpha(
            Color.getHSBColor(hue, saturation, brightness),
            Render2DUtils.limit((alpha * 255).toDouble())
        )
    }

    constructor(color: Color) {
        val col = Color.RGBtoHSB(color.red, color.green, color.blue, floatArrayOf(hue, saturation, brightness))
        hue = col[0]
        saturation = col[1]
        brightness = col[2]
        this.color = color
        alpha = color.alpha / 255f
    }

    val rGB: Int
        get() = color.rgb

    fun setColor(hue: Float, saturation: Float, brightness: Float, alpha: Float) {
        this.hue = hue
        this.saturation = saturation
        this.brightness = brightness
        this.alpha = alpha
        color = Render2DUtils.reAlpha(
            Color.getHSBColor(hue, saturation, brightness),
            Render2DUtils.limit((alpha * 255).toDouble())
        )
    }
}
