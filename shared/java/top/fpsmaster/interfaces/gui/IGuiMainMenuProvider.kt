package top.fpsmaster.interfaces.gui

import net.minecraft.client.gui.GuiScreen
import top.fpsmaster.interfaces.IProvider

interface IGuiMainMenuProvider : IProvider {
    fun initGui()
    fun renderSkybox(mouseX: Int, mouseY: Int, partialTicks: Float, width: Int, height: Int, zLevel: Float)
    fun showSinglePlayer(screen: GuiScreen)
}