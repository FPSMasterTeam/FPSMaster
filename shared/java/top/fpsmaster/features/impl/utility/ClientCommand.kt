package top.fpsmaster.features.impl.utility

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.TextSetting

class ClientCommand : Module("ClientCommand", Category.Utility) {
    init {
        addSettings(prefix)
    }

    override fun onEnable() {
        using = true
        super.onEnable()
    }

    override fun onDisable() {
        using = false
        super.onDisable()
    }

    companion object {
        var using = false
        val prefix = TextSetting("prefix", "#")
    }
}