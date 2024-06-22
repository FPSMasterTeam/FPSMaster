package top.fpsmaster.features.impl.utility

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.utils.thirdparty.rawinput.RawInputMod

class RawInput : Module("RawInput", Category.Utility) {
    private var rawInputMod = RawInputMod()
    override fun onEnable() {
        super.onEnable()
        rawInputMod.start()
    }

    override fun onDisable() {
        super.onDisable()
        rawInputMod.stop()
    }
}
