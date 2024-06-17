package top.fpsmaster.features.impl.render

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import java.awt.Color

class FireModifier : Module("FireModifier", Category.RENDER) {
    init {
        addSettings(height, customColor, colorSetting)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    companion object {
        @JvmField
        var using = false
        @JvmField
        var height = NumberSetting("Height", 0.5, 0, 0.7, 0.1)
        @JvmField
        var colorSetting = ColorSetting("Color", Color(255, 0, 0)){ customColor.value}
        @JvmField
        var customColor = BooleanSetting("CustomColor", false)
    }
}
