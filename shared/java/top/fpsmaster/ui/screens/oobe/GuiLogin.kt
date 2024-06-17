package top.fpsmaster.ui.screens.oobe

import net.minecraft.client.gui.GuiScreen
import top.fpsmaster.ui.screens.oobe.impls.Login

class GuiLogin : GuiScreen() {

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        login.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        login.mouseClick(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        login.keyTyped(typedChar, keyCode)
    }

    companion object {
        var login = Login(false)
    }
}
