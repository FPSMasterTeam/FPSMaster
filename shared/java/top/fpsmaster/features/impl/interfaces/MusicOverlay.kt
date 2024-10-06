package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender2D
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.modules.music.IngameOverlay
import top.fpsmaster.modules.music.JLayerHelper.updateLoudness
import top.fpsmaster.utils.math.MathTimer
import java.awt.Color

class MusicOverlay : InterfaceModule("MusicDisplay", Category.Interface) {
    init {
        addSettings(backgroundColor, progressColor, color, amplitude, bg, rounded, roundRadius)
    }

    companion object {
        var amplitude = NumberSetting("Amplitude", 10, 0, 10, 0.1)
        var progressColor = ColorSetting("ProgressColor", Color(255, 255, 255, 100))
        var color = ColorSetting("Visual", Color(255, 255, 255, 100))
    }

    val timer: MathTimer = MathTimer()

    @Subscribe
    fun onRender(e: EventRender2D) {
        if (timer.delay(100)) {
            updateLoudness()
        }
        IngameOverlay.onRender()
    }
}
