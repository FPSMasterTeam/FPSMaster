package top.fpsmaster.interfaces.gui

import net.minecraft.entity.Entity

interface IGuiIngameProvider {
    fun drawHealth(entityIn: Entity)
}