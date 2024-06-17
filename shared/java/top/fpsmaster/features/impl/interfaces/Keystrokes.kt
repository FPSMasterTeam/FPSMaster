package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import java.awt.Color

class Keystrokes : InterfaceModule("Keystrokes", Category.Interface) {
    init {
        addSettings(rounded, backgroundColor, fontShadow, betterFont, pressedColor, bg, rounded , roundRadius)
    }

    companion object {
        @JvmField
        var pressedColor = ColorSetting("PressedColor", Color(255, 255, 255, 120))
    }
}
