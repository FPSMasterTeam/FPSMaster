package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import java.awt.Color

class FPSDisplay : InterfaceModule("FPSDisplay", Category.Interface) {
    init {
        addSettings(textColor)
        addSettings(rounded, backgroundColor, fontShadow, betterFont, bg, rounded , roundRadius)
    }

    companion object {
        @JvmField
        var textColor = ColorSetting("TextColor", Color(255, 255, 255))
    }
}
