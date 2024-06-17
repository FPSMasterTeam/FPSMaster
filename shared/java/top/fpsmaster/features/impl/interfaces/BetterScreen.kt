package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.BooleanSetting

class BetterScreen : InterfaceModule("BetterScreen", Category.Interface) {
    init {
        addSettings(useBG, backgroundAnimation, noFlickering)
    }

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
        @JvmField
        val useBG = BooleanSetting("Background", true)
        @JvmField
        val backgroundAnimation = BooleanSetting("BackgroundAnimation", true)
        @JvmField
        val noFlickering = BooleanSetting("noFlickering", true)
    }
}