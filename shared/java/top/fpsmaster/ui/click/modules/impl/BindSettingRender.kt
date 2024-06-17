package top.fpsmaster.ui.click.modules.impl

import org.lwjgl.input.Keyboard
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BindSetting
import top.fpsmaster.ui.click.MainPanel
import top.fpsmaster.ui.click.modules.SettingRender
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.render.Render2DUtils
import java.util.*

class BindSettingRender(module: Module, setting: BindSetting) : SettingRender<BindSetting>(setting) {
    var colorAnimation = ColorAnimation()

    init {
        mod = module
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
            )], x + 10, y + 2, FPSMaster.theme.getTextColorTitle().rgb
        ).toFloat()
        val keyName = Keyboard.getKeyName(setting.value)
        val s16b = FPSMaster.INSTANCE.fontManager!!.s16
        val width1 = 10 + s16b.getStringWidth(keyName)
        if (Utility.isHovered(x + 15 + fw, y, width1.toFloat(), 14f, mouseX.toInt(), mouseY.toInt())) {
            Render2DUtils.drawOptimizedRoundedRect(
                x + 14.5f + fw,
                y - 0.5f,
                (width1 + 1).toFloat(),
                13f,
                FPSMaster.theme.getModeBoxBorder()
            )
        }
        Render2DUtils.drawOptimizedRoundedRect(x + 15 + fw, y, width1.toFloat(), 12f, colorAnimation.color)
        s16b.drawString(keyName, x + 18 + fw, y + 2, FPSMaster.theme.getTextColorTitle().rgb)
        if (MainPanel.bindLock == setting.name) {
            colorAnimation.base(FPSMaster.theme.getModeBoxBorder())
        } else {
            colorAnimation.base(FPSMaster.theme.getModeBox())
        }
        this.height = 16f
    }

    override fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int) {
        val fw = FPSMaster.INSTANCE.fontManager!!.s16.getStringWidth(
            FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )]
        ).toFloat()
        val keyName = Keyboard.getKeyName(setting.value)
        val s16b = FPSMaster.INSTANCE.fontManager!!.s16
        if (Utility.isHovered(
                x + 12 + fw,
                y,
                10f + s16b.getStringWidth(keyName),
                12f,
                mouseX.toInt(),
                mouseY.toInt()
            ) && btn == 0
        ) {
            if (MainPanel.bindLock.isEmpty()) {
                MainPanel.bindLock = setting.name
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (MainPanel.bindLock == setting.name) {
            setting.value = Keyboard.getEventKey()
            MainPanel.bindLock = ""
        }
    }
}
