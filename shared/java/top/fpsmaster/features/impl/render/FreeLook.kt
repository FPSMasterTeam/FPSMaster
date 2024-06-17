package top.fpsmaster.features.impl.render

import org.lwjgl.input.Keyboard
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender3D
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BindSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.mods.WrapperFreeLook

class FreeLook : Module("FreeLook", Category.RENDER) {
    private var bind = BindSetting("bind", Keyboard.KEY_LMENU)

    init {
        addSettings(bind)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    @Subscribe
    fun onRender3D(e: EventRender3D?) {
        if (mc.currentScreen != null) return
        if (!perspectiveToggled) {
            if (Keyboard.isKeyDown(bind.value)) {
                perspectiveToggled = true
                cameraYaw = ProviderManager.mcProvider.getPlayer()!!.rotationYaw
                cameraPitch = ProviderManager.mcProvider.getPlayer()!!.rotationPitch
                previousPerspective = mc.gameSettings.hideGUI
                mc.gameSettings.thirdPersonView = 1
            }
        } else if (!Keyboard.isKeyDown(bind.value)) {
            perspectiveToggled = false
            mc.gameSettings.thirdPersonView = if (previousPerspective) 1 else 0
        }
    }

    companion object {
        @JvmField
        var using = false
        @JvmField
        var perspectiveToggled = false
        @JvmField
        var cameraYaw = 0f
        @JvmField
        var cameraPitch = 0f
        private var previousPerspective = false
        @JvmStatic
        fun getCameraYaw(): Float {
            return WrapperFreeLook.getCameraYaw()
        }

        @JvmStatic
        fun getCameraPitch(): Float {
            return WrapperFreeLook.getCameraPitch()
        }

        @JvmStatic
        val cameraPrevYaw: Float
            get() = WrapperFreeLook.getCameraPrevYaw()
        @JvmStatic
        val cameraPrevPitch: Float
            get() = WrapperFreeLook.getCameraPrevPitch()

        @JvmStatic
        fun overrideMouse(): Boolean {
            return WrapperFreeLook.overrideMouse()
        }
    }
}
