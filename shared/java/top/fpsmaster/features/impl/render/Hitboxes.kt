package top.fpsmaster.features.impl.render

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender3D
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.wrapper.mods.WrapperHitboxes
import java.awt.Color

class Hitboxes : Module("HitBoxes", Category.RENDER) {
    var color = ColorSetting("Color", Color(255, 255, 255, 255))

    init {
        addSettings(color)
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    @Subscribe
    fun onRender(event: EventRender3D?) {
        WrapperHitboxes.render(event, color)
    }

    companion object {
        var using = false
    }
}
