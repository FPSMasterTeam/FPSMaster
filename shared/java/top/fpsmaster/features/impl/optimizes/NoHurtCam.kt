package top.fpsmaster.features.impl.optimizes

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module

class NoHurtCam : Module("NoHurtCam", Category.OPTIMIZE) {
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
