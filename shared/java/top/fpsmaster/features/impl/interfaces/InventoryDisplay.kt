package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category

class InventoryDisplay : InterfaceModule("InventoryDisplay", Category.Interface) {
    init {
        addSettings(rounded, backgroundColor, bg, rounded , roundRadius)
    }
}
