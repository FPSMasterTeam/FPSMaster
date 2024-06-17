package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import java.awt.Color

class LyricsDisplay : InterfaceModule("LyricsDisplay", Category.Interface) {
    init {
        addSettings(backgroundColor, rounded, betterFont, textColor, textBG, bg, rounded , roundRadius)
    }

    companion object {
        @JvmField
        var textColor = ColorSetting("TextColor", Color(255, 255, 255))
        @JvmField
        var textBG = ColorSetting("TextColorBG", Color(255, 255, 255))
    }
}
