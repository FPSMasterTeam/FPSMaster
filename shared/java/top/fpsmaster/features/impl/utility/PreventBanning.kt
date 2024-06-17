package top.fpsmaster.features.impl.utility

import net.minecraft.item.ItemSword
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.ModeSetting
import top.fpsmaster.interfaces.ProviderManager

class PreventBanning : Module("PreventBanning", Category.Utility) {
    init {
        addSettings(mode)
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
        var mode = ModeSetting("mode", 1, "Falling", "Air", "All")
        @JvmStatic
        fun canBlock(): Boolean {
            return if (ProviderManager.mcProvider.getPlayerHeldItem()!!.maxItemUseDuration > 0 && ProviderManager.mcProvider.getPlayerHeldItem()!!.item is ItemSword) {
                if (mode.mode == 0) {
                    return ProviderManager.mcProvider.getPlayer()!!.fallDistance < 1
                } else if (mode.mode == 1) {
                    return ProviderManager.mcProvider.getPlayer()!!.onGround
                }
                false
            } else {
                true
            }
        }
    }
}
