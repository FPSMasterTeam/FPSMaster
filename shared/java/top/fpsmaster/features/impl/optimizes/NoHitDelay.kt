package top.fpsmaster.features.impl.optimizes

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.interfaces.ProviderManager

class NoHitDelay : Module("NoHitDelay", Category.OPTIMIZE) {
    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    @Subscribe
    fun onTick(e: EventTick){
        ProviderManager.mcProvider.removeClickDelay()
    }

    companion object {
        var using = false
    }
}
