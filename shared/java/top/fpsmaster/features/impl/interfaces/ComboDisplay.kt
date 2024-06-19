package top.fpsmaster.features.impl.interfaces

import net.minecraft.entity.Entity
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventAttack
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.interfaces.ProviderManager
import java.awt.Color

class ComboDisplay : InterfaceModule("ComboDisplay", Category.Interface) {
    var target: Entity? = null

    init {
        addSettings(textColor, backgroundColor, betterFont, fontShadow, rounded, bg, rounded , roundRadius)
    }

    @Subscribe
    fun onTick(e: EventTick) {
        if (ProviderManager.mcProvider.getPlayer() == null) return
        if (ProviderManager.mcProvider.getPlayer()!!.hurtTime == 1 || target != null && ProviderManager.utilityProvider.getDistanceToEntity(
                ProviderManager.mcProvider.getPlayer(),
                target
            ) > 5
        ) {
            combo = 0
        }
        if (target != null && target!!.isEntityAlive && target!!.hurtResistantTime == 19) {
            combo++
        }
    }

    @Subscribe
    fun attack(e: EventAttack) {
        target = e.target
    }

    companion object {
        @JvmField
        var combo = 0
        @JvmField
        var textColor = ColorSetting("TextColor", Color(255, 255, 255))
    }
}
