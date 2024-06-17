package top.fpsmaster.ui.screens.oobe

open class Scene {
    open fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {}
    open fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int) {}
    open fun keyTyped(typedChar: Char, keyCode: Int) {}
}
