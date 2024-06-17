package top.fpsmaster.interfaces.game

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import top.fpsmaster.interfaces.IProvider

interface IUtilityProvider : IProvider {
    fun getResourcePath(resourceLocation: ResourceLocation?): String?


    fun getDistanceToEntity(e1: Entity?, e2: Entity?): Double

    fun isItemEnhancementEmpty(i: ItemStack?): Boolean

    fun getPotionIconIndex(effect: PotionEffect?): Int

    fun makeChatComponent(msg: String?): Any
}