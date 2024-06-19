package top.fpsmaster.features.impl.utility

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventUpdate
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.interfaces.ProviderManager

class Sprint : Module("Sprint", Category.Utility) {
    @Subscribe
    fun onUpdate(e: EventUpdate) {
        ProviderManager.gameSettings.setKeyPress(mc.gameSettings.keyBindSprint, true)
    }

    override fun onDisable() {
        super.onDisable()
        ProviderManager.gameSettings.setKeyPress(mc.gameSettings.keyBindSprint, false)
    }
}
