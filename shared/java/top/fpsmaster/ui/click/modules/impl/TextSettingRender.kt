package top.fpsmaster.ui.click.modules.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.TextSetting
import top.fpsmaster.ui.common.TextField
import top.fpsmaster.ui.click.modules.SettingRender
import top.fpsmaster.utils.Utility
import java.awt.Color
import java.util.*
import kotlin.math.min

class TextSettingRender(mod: Module, setting: TextSetting) : SettingRender<TextSetting>(setting) {
    var inputBox: TextField

    init {
        this.mod = mod
        inputBox = TextField(FPSMaster.INSTANCE.fontManager!!.s16, false, "输入名称", -1, Color(50, 50, 50).rgb, 1500)
        inputBox.text = setting.value
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
        inputBox.backGroundColor = FPSMaster.theme.textboxEnabled.rgb
        inputBox.fontColor = FPSMaster.theme.textColorTitle.rgb
        val s16 = FPSMaster.INSTANCE.fontManager!!.s16
        val text = FPSMaster.INSTANCE.i18n[(mod.name + "." + setting.name).lowercase(Locale.getDefault())]
        s16.drawString(text, x + 18, y + 6, FPSMaster.theme.getTextColorDescription().rgb)
        inputBox.drawTextBox(
            x + s16.getStringWidth(text) + 20,
            y + 2,
            min(200f, FPSMaster.INSTANCE.fontManager!!.s18.getStringWidth(inputBox.text) + 20f),
            16f
        )
        this.height = 24f
    }

    override fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int) {
        if (Utility.isHovered(x, y, width, height, mouseX.toInt(), mouseY.toInt())) {
            inputBox.mouseClicked(mouseX.toInt(), mouseY.toInt(), btn)
        } else {
            inputBox.setFocused(false)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        inputBox.textboxKeyTyped(typedChar, keyCode)
        setting.value = inputBox.text
    }
}
