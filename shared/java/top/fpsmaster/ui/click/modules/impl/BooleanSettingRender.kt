package top.fpsmaster.ui.click.modules.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.ui.click.modules.SettingRender
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.math.animation.Type
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color
import java.util.*

class BooleanSettingRender(mod: Module, setting: BooleanSetting) : SettingRender<BooleanSetting>(setting) {
    // animation
    private var box = ColorAnimation(Color(255, 255, 255, 0))

    init {
        this.mod = mod
    }

    override fun render(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        mouseX: Float,
        mouseY: Float,
        custom: Boolean
    ) {
        box.update()
        if (setting.value) {
            box.start(box.color, FPSMaster.theme.getPrimary(), 0.2f, Type.EASE_IN_OUT_QUAD)
        } else {
            box.start(box.color, FPSMaster.theme.getCheckboxBox(), 0.2f, Type.EASE_IN_OUT_QUAD)
        }
        Render2DUtils.drawOptimizedRoundedRect(x + 14, y + 3, 6f, 6f, 3, box.color.rgb)
        FPSMaster.INSTANCE.fontManager!!.s16.drawString(
            FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )], x + 26, y + 1, FPSMaster.theme.getTextColorDescription().rgb
        )
        this.height = 12f
    }

    override fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int) {
        if (Utility.isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt())) {
            setting.toggle()
        }
    }
}
