package top.fpsmaster.features.impl.utility

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.TextSetting
import top.fpsmaster.interfaces.ProviderManager

class NameProtect : Module("NameProtect", Category.Utility) {
    init {
        addSettings(Companion.name)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    @Subscribe
    fun onTick(e: EventTick) {
        if (ProviderManager.mcProvider.getPlayer() != null) {
            playerName = ProviderManager.mcProvider.getPlayer()!!.name
            replacement = Companion.name.value.replace("&".toRegex(), "§")
        }
    }

    companion object {
        var name = TextSetting("Name", "Hide")
        var using = false
        private var playerName: String? = ""
        private var replacement: String? = ""
        fun filter(s: String): String {
            return if (using && ProviderManager.mcProvider.getPlayer() != null) {
                if (playerName == null) return s
                if (replacement == null) s else s.replace(
                    playerName!!.toRegex(),
                    replacement!!
                )
            } else {
                s
            }
        }
    }
}
