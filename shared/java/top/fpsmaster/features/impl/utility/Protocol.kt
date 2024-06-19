package top.fpsmaster.features.impl.utility

import net.minecraft.network.NetworkManager
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventCustomPacket
import top.fpsmaster.event.events.EventSendChatMessage
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.modules.logger.Logger.info
import top.fpsmaster.wrapper.HYTProvider

class Protocol : Module("Protocol", Category.Utility) {
    init {
        addSettings(bypass, chestfix)
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
    fun onPacket(e: EventCustomPacket?) {
        // check server ip
        HYTProvider.onPacket(e)
    }

    @Subscribe
    fun onChat(e: EventSendChatMessage) {
        if (e.msg.contains("/kh")) {
            info("打开组队页面")
            HYTProvider.sendOpenParty()
            e.cancel()
        }
    }

    companion object {
        @JvmStatic
        fun sendBypass(netManager: NetworkManager) {
            if (!bypass.value)
                return
            HYTProvider.sendBypass(netManager)
        }

        @JvmField
        var bypass = BooleanSetting("Bypass", true)

        @JvmField
        var chestfix = BooleanSetting("ChestFix", true)

        @JvmField
        var using = false
    }
}
