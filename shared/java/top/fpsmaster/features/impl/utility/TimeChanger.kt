package top.fpsmaster.features.impl.utility

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventPacket
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.WorldClientProvider
import top.fpsmaster.wrapper.packets.SPacketTimeUpdateProvider

class TimeChanger : Module("TimeChanger", Category.Utility) {
    var time = NumberSetting("Time", 0, 0, 24000, 1)

    init {
        addSettings(time)
    }

    @Subscribe
    fun onTick(e: EventTick?) {
        if (ProviderManager.worldClientProvider.getWorld() != null) ProviderManager.worldClientProvider.setWorldTime(time.value.toLong())
    }

    @Subscribe
    fun onPacket(e: EventPacket) {
        if (e.type === EventPacket.PacketType.RECEIVE) {
            if (ProviderManager.packetTimeUpdate.isPacket(e.packet)) e.cancel()
        }
    }
}
