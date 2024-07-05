package top.fpsmaster.features.impl.interfaces

import net.minecraft.client.gui.ScaledResolution
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender2D
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.ui.Compass
import top.fpsmaster.utils.Utility

class DirectionDisplay : Module("DirectionDisplay", Category.Interface) {
    val compass = Compass(325f, 325f, 1f, 2, true)

    @Subscribe
    fun onRender(e: EventRender2D){
        val scaledResolution = ScaledResolution(Utility.mc)
        compass.draw(scaledResolution)
    }
}
