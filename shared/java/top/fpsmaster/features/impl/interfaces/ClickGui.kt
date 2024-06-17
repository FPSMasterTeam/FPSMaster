package top.fpsmaster.features.impl.interfaces

import org.lwjgl.input.Keyboard
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.BindSetting

class ClickGui : InterfaceModule("ClickGui", Category.Interface) {
    init {
        addSettings(keyBind)
    }

    override fun onEnable() {
        super.onEnable()
        this.set(false)
    }

    override fun onDisable() {
        super.onDisable()
    }

    companion object {
        var keyBind = BindSetting("Key", Keyboard.KEY_RSHIFT)
    }
}
