package top.fpsmaster.features.settings.impl

import top.fpsmaster.features.settings.Setting
import top.fpsmaster.features.settings.impl.utils.CustomColor
import java.awt.Color

class ColorSetting : Setting<CustomColor> {

    constructor(name: String, value: CustomColor, visible: () -> Boolean) : super(name, value, visible) {
        this.visible = visible
    }
    constructor(name: String, value: Color, visible: () -> Boolean) : super(name, CustomColor(value), visible) {
        this.visible = visible
    }
    constructor(name: String, value: CustomColor) : super(name, value)
    constructor(name: String, value: Color) : super(name, CustomColor(value))

    val rGB: Int
        get() = value.rGB
    val color: Color
        get() = value.color
}
