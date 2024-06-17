package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.BooleanSetting

class Scoreboard : InterfaceModule("Scoreboard", Category.Interface) {
    init {
        addSettings(rounded, backgroundColor, fontShadow, betterFont, score, bg, rounded , roundRadius)
    }

    override fun onEnable() {
        using = true
        super.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    companion object {
        @JvmField
        var using = false
        @JvmField
        var score = BooleanSetting("Score", false)
    }
}
