package top.fpsmaster.features.impl.render

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module

class MinimizedBobbing : Module("MinimizedBobbing", Category.RENDER) {
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
