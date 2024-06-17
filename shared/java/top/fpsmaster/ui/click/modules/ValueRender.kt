package top.fpsmaster.ui.click.modules

import top.fpsmaster.features.manager.Module

abstract class ValueRender {
    open lateinit var mod: Module
    @JvmField
    var height = 0f
    abstract fun render(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, custom: Boolean)
    abstract fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int)
    abstract fun keyTyped(typedChar: Char, keyCode: Int)
}
