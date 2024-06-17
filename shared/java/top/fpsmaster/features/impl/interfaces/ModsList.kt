package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ColorSetting
import java.awt.Color

class ModsList : InterfaceModule("ModsList", Category.Interface) {
    var showLogo = BooleanSetting("ShowLogo", true)
    var english = BooleanSetting("English", true)
    var rainbow = BooleanSetting("Rainbow", true)
    var color = ColorSetting("Color", Color(255, 255, 255)) { !rainbow.value }

    init {
        addSettings(showLogo, english, color, rainbow, betterFont, backgroundColor, bg)
    }
}