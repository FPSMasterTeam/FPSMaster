package top.fpsmaster.features.impl.render

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module

class FullBright : Module("FullBright", Category.RENDER) {
    private var oldGamma = 0f
    override fun onEnable() {
        super.onEnable()
        oldGamma = mc.gameSettings.gammaSetting
        mc.gameSettings.gammaSetting = 100f
    }

    override fun onDisable() {
        super.onDisable()
        mc.gameSettings.gammaSetting = oldGamma
    }
}
