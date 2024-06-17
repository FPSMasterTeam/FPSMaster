package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category

class HideIndicator : InterfaceModule("HideIndicator", Category.Interface) {
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
    }
}
