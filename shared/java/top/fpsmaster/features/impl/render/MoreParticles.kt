package top.fpsmaster.features.impl.render

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumParticleTypes
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventAttack
import top.fpsmaster.event.events.EventUpdate
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ModeSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.WrapperEntityLightningBolt
import top.fpsmaster.wrapper.sound.SoundProvider

class MoreParticles : Module("MoreParticles", Category.RENDER) {
    private var target: Entity? = null
    private var lastEffect: Entity? = null

    init {
        addSettings(sharpness, alwaysSharpness, crit, alwaysCrit, special, killEffect)
    }

    @Subscribe
    fun onUpdate(e: EventUpdate?) {
        if (target !is EntityLivingBase)
            return
        val entityLivingBase = target as EntityLivingBase
        if (lastEffect !== target && (entityLivingBase.health <= 0 || !entityLivingBase.isEntityAlive)) {
            if (killEffect.value == 1) {
                // summon lightning effect
                ProviderManager.worldClientProvider.addWeatherEffect(
                    WrapperEntityLightningBolt(
                        ProviderManager.worldClientProvider.getWorld(),
                        entityLivingBase.posX,
                        entityLivingBase.posY,
                        entityLivingBase.posZ,
                        false
                    )
                )
                SoundProvider.playLightning(
                    entityLivingBase.posX,
                    entityLivingBase.posY,
                    entityLivingBase.posZ,
                    1,
                    1.0f,
                    false
                )
            } else if (killEffect.value == 2) {
                // explosion
                mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.EXPLOSION_LARGE)
                SoundProvider.playExplosion(
                    entityLivingBase.posX,
                    entityLivingBase.posY,
                    entityLivingBase.posZ,
                    1,
                    1.0f,
                    false
                )
            }
            lastEffect = target
            target = null
        }
    }

    @Subscribe
    fun onAttack(e: EventAttack) {
        if (e.target.isEntityAlive) {
            target = e.target
            if (ProviderManager.mcProvider.getPlayer()!!.fallDistance != 0f || alwaysCrit.value) {
                for (i in 0 until crit.value.toInt()) {
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT)
                }
            }
            var needSharpness = false
            if (ProviderManager.mcProvider.getPlayer()!!.inventory.getCurrentItem() != null) needSharpness =
                !ProviderManager.utilityProvider.isItemEnhancementEmpty(ProviderManager.mcProvider.getPlayer()!!.inventory.getCurrentItem()) && ProviderManager.mcProvider.getPlayer()!!.inventory.getCurrentItem().enchantmentTagList.toString()
                    .contains("id:16s")
            if (needSharpness || alwaysSharpness.value) {
                for (i in 0 until sharpness.value.toInt()) {
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT_MAGIC)
                }
            }
            if (special.value == 1) {
                mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.HEART)
            } else if (special.value == 2) {
                mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.FLAME)
            }
        }
    }

    companion object {
        var sharpness = NumberSetting("Sharpness", 2, 0, 30, 1)
        var alwaysSharpness = BooleanSetting("AlwaysSharpness", false)
        var crit = NumberSetting("Crit", 2, 0, 30, 1)
        var alwaysCrit = BooleanSetting("AlwaysCrit", false)
        var special = ModeSetting("Special", 0, "None", "Heart", "Flame")
        var killEffect = ModeSetting("killEffect", 0, "None", "Lightning", "Explosion")
    }
}
