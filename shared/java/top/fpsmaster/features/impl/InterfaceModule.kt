package top.fpsmaster.features.impl

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import java.awt.Color

open class InterfaceModule(name: String, category: Category) : Module(name, category) {
    @JvmField
    var rounded = BooleanSetting("Round", true)

    @JvmField
    var roundRadius = NumberSetting("RoundRadius", 3, 0, 30, 1) { rounded.value }

    @JvmField
    var betterFont = BooleanSetting("BetterFont", false)

    @JvmField
    var fontShadow = BooleanSetting("FontShadow", true) { betterFont.value }

    @JvmField
    var bg = BooleanSetting("Background", true)

    @JvmField
    var backgroundColor = ColorSetting("BackgroundColor", Color(0, 0, 0, 0)) { bg.value }
}
