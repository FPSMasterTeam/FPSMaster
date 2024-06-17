package top.fpsmaster.ui.click.modules.impl

import org.lwjgl.input.Mouse
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.ui.click.MainPanel
import top.fpsmaster.ui.click.modules.SettingRender
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.render.Render2DUtils
import java.util.*

class NumberSettingRender(mod: Module, setting: NumberSetting) : SettingRender<NumberSetting>(setting) {
    // animation
    var aWidth = 0f
    var dragging = false

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
        val fw = FPSMaster.INSTANCE.fontManager!!.s16.drawString(
            FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )], x + 10, y + 2, FPSMaster.theme.getTextColorDescription().rgb
        ).toFloat()
        Render2DUtils.drawOptimizedRoundedRect(x + 16 + fw, y + 3, 160f, 6f, FPSMaster.theme.getFrontBackground().rgb)
        val percent =
            (setting.value.toFloat() - setting.min.toFloat()) / (setting.max.toFloat() - setting.min.toFloat())
        aWidth = base(aWidth.toDouble(), (160 * percent).toDouble(), 0.2).toFloat()
        Render2DUtils.drawOptimizedRoundedRect(x + 16 + fw, y + 3, aWidth, 6f, FPSMaster.theme.getPrimary().rgb)
        FPSMaster.INSTANCE.fontManager!!.s16.drawString(
            setting.value.toString(),
            x + fw + 20 + 160,
            y + 2,
            FPSMaster.theme.getTextNumber().rgb
        )
        if (!Mouse.isButtonDown(0)) MainPanel.dragLock = "null"
        if (MainPanel.dragLock == mod.name + setting.name + 4) {
            val v = mouseX - x - 16 - FPSMaster.INSTANCE.fontManager!!.s16.getStringWidth(
                FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(
                    Locale.getDefault()
                )]
            )
            val mPercent = v / 160
            val newValue = (setting.max.toFloat() - setting.min.toFloat()) * mPercent + setting.min.toFloat()
            setting.value = newValue
        }
        this.height = 12f
    }

    override fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int) {
        val fw = FPSMaster.INSTANCE.fontManager!!.s16.drawString(
            FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )], x + 10, y + 2, FPSMaster.theme.getTextColorDescription().rgb
        ).toFloat()
        if (Utility.isHovered(x + 16 + fw, y, 160f, height, mouseX.toInt(), mouseY.toInt()) && Mouse.isButtonDown(0)) {
            if (btn == 0 && MainPanel.dragLock == "null") {
                MainPanel.dragLock = mod.name + setting.name + 4
                dragging = true
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}
