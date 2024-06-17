package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category

class PlayerDisplay : InterfaceModule("PlayerDisplay", Category.Interface) {
    init {
        addSettings(betterFont, rounded, fontShadow, backgroundColor, bg, rounded , roundRadius)
    }
}
