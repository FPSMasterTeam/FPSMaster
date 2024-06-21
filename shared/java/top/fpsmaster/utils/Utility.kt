package top.fpsmaster.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import top.fpsmaster.FPSMaster
import top.fpsmaster.interfaces.ProviderManager
import java.lang.reflect.Field

open class Utility {
    companion object {
        @JvmField
        var mc: Minecraft = Minecraft.getMinecraft()

        @JvmStatic
        fun sendClientMessage(msg: String?) {
            if (ProviderManager.mcProvider.getWorld() != null) {
                ProviderManager.mcProvider.printChatMessage(ProviderManager.utilityProvider.makeChatComponent(msg))
            }
        }
    }
}
