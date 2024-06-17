package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting

class CoordsDisplay : InterfaceModule("CoordsDisplay", Category.Interface) {
    val limitDisplay = BooleanSetting("LimitDisplay", false)
    val limitDisplayY = NumberSetting("LimitDisplayY", 92, 0, 255, 1)

    init {
        addSettings(FPSDisplay.textColor)
        addSettings(rounded, backgroundColor, fontShadow, betterFont, bg, rounded , roundRadius)
        addSettings(limitDisplay, limitDisplayY)
    }
}