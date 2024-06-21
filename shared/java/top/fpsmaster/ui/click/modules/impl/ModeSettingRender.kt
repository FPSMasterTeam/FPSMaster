package top.fpsmaster.ui.click.modules.impl

import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.ModeSetting
import top.fpsmaster.ui.click.modules.SettingRender
import top.fpsmaster.utils.Utility.isHovered
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.render.Render2DUtils
import java.util.*

class ModeSettingRender(mod: Module, setting: ModeSetting) : SettingRender<ModeSetting>(setting) {
    private var expand = false
    private var expandH = 0f

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
        val fw = FPSMaster.fontManager.s16.drawString(
            FPSMaster.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )], x + 10, y + 8, FPSMaster.theme.getTextColorDescription().rgb
        ).toFloat()
        val maxWidth = 80f
        Render2DUtils.drawOptimizedRoundedBorderRect(
            x + 16 + fw,
            y + 4,
            maxWidth,
            16 + expandH,
            0.5f,
            FPSMaster.theme.getModeBox(),
            FPSMaster.theme.getModeBoxBorder()
        )
        FPSMaster.fontManager.s18.drawString(
            FPSMaster.i18n[(mod.name + "." + setting.name + "." + setting.modeName).lowercase(
                Locale.getDefault()
            )], x + 20 + fw, y + 7, FPSMaster.theme.getTextColorTitle().rgb
        )

        // rotate this icon
        GL11.glPushMatrix()
        val rotatePercent = expandH / (setting.modesSize * 14)
        GL11.glTranslatef(x + 16 + fw + maxWidth - 12, y + 12, 0f)
        GL11.glRotatef(rotatePercent * 180, 0f, 0f, 1f)
        GL11.glTranslatef(-(x + 16 + fw + maxWidth - 12), -(y + 12), 0f)
        Render2DUtils.drawImage(
            ResourceLocation("client/gui/settings/icons/arrow.png"),
            x + 16 + fw + maxWidth - 16,
            y + 8,
            8f,
            8f,
            FPSMaster.theme.getTextColorTitle()
        )
        GL11.glPopMatrix()
        if (expand) {
            expandH = base(expandH.toDouble(), (setting.modesSize * 14).toDouble(), 0.2).toFloat()
            for (i in 1..setting.modesSize) {
                if (isHovered(x + 20 + fw, y + 4 + i * 14, maxWidth, 16f, mouseX.toInt(), mouseY.toInt())) {
                    FPSMaster.fontManager.s16.drawString(
                        FPSMaster.i18n[(mod.name + "." + setting.name + "." + setting.getMode(i)).lowercase(
                            Locale.getDefault()
                        )], x + 20 + fw, y + 7 + i * 14, FPSMaster.theme.getTextColorDescriptionHover().rgb
                    )
                } else {
                    FPSMaster.fontManager.s16.drawString(
                        FPSMaster.i18n[(mod.name + "." + setting.name + "." + setting.getMode(i)).lowercase(
                            Locale.getDefault()
                        )], x + 20 + fw, y + 7 + i * 14, FPSMaster.theme.getTextColorDescription().rgb
                    )
                }
            }
        } else {
            expandH = base(expandH.toDouble(), 0.0, 0.2).toFloat()
        }
        this.height = 24 + expandH
    }

    override fun mouseClick(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, btn: Int) {
        val fw = FPSMaster.fontManager.s16.drawString(
            FPSMaster.i18n[(mod.name + "." + setting.name).lowercase(
                Locale.getDefault()
            )], x + 10, y + 8, FPSMaster.theme.getTextColorDescription().rgb
        ).toFloat()
        val maxWidth = 80f
        if (isHovered(x + 16 + fw, y + 4, maxWidth, 16f, mouseX.toInt(), mouseY.toInt())) {
            expand = !expand
        }
        if (expand) {
            for (i in 1..setting.modesSize) {
                if (isHovered(x + 20 + fw, y + 7 + i * 14, maxWidth, 16f, mouseX.toInt(), mouseY.toInt())) {
                    setting.value = i - 1
                    expand = false
                }
            }
        }
    }
}
