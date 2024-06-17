package top.fpsmaster.interfaces.game

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.effect.EntityLightningBolt
import top.fpsmaster.wrapper.blockpos.WrapperBlockPos
import top.fpsmaster.wrapper.util.WrapperAxisAlignedBB

interface IWorldClientProvider {
    fun getBlockState(pos: WrapperBlockPos?): IBlockState?

    fun getBlock(pos: WrapperBlockPos?): Block?

    fun getBlockBoundingBox(pos: WrapperBlockPos?, state: IBlockState?): WrapperAxisAlignedBB

    fun addWeatherEffect(entityLightningBolt: EntityLightningBolt?)

    fun getWorld(): WorldClient?

    fun setWorldTime(l: Long)
}