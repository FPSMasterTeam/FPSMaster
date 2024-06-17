package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import java.awt.Color

class MusicOverlay : InterfaceModule("MusicDisplay", Category.Interface) {
    init {
        addSettings(backgroundColor, progressColor, color, amplitude, bg, rounded , roundRadius)
    }

    companion object {
        var amplitude = NumberSetting("Amplitude", 10, 0, 10, 0.1)
        var progressColor = ColorSetting("ProgressColor", Color(255, 255, 255, 100))
        var color = ColorSetting("Visual", Color(255, 255, 255, 100))
    }
}
