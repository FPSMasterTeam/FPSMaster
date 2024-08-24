package top.fpsmaster.interfaces.render

import net.minecraft.block.state.IBlockState
import top.fpsmaster.wrapper.blockpos.WrapperBlockPos

interface IEffectRendererProvider {
    fun addRedStoneBreak(pos: WrapperBlockPos)
}