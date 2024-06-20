package top.fpsmaster.features.impl.utility

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ModeSetting

class LevelTag : Module("Nametags", Category.Utility) {
    init {
        addSettings(showSelf)
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
        var showSelf = BooleanSetting("ShowSelf", true)
        @JvmField
        var health = BooleanSetting("Health", true)
        var levelMode = ModeSetting("RankMode", 0, "None", "Bedwars", "Bedwars-xp", "Skywars", "Kit")
        @JvmField
        var using = false
    }
}
