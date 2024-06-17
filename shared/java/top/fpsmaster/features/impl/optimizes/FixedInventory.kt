package top.fpsmaster.features.impl.optimizes

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module

class FixedInventory : Module("FixedInventory", Category.OPTIMIZE) {
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
