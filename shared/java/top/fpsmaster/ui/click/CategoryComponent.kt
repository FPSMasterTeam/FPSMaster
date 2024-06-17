package top.fpsmaster.ui.click

import net.minecraft.util.ResourceLocation
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.manager.Category
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.math.animation.Type
import top.fpsmaster.utils.render.Render2DUtils
import java.util.*

class CategoryComponent(@JvmField var category: Category) {
    private var animationName = ColorAnimation()
    @JvmField
    var categorySelectionColor = ColorAnimation()

    init {
        animationName.color = FPSMaster.theme.getCategoryText()
    }

    fun render(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float, selected: Boolean) {
        animationName.start(
            animationName.color,
            if (selected) FPSMaster.theme.getCategoryTextSelected() else FPSMaster.theme.getCategoryText(),
            0.2f,
            Type.EASE_IN_OUT_QUAD
        )
        animationName.update()
        Render2DUtils.drawImage(
            ResourceLocation("client/gui/settings/icons/" + category.category.lowercase() + ".png"),
            x + 12,
            y - 2,
            12f,
            12f,
            animationName.color
        )
        FPSMaster.INSTANCE.fontManager!!.s16.drawString(
            FPSMaster.INSTANCE.i18n["category." + category.category.lowercase(
                Locale.getDefault()
            )], x + 30, y, animationName.color.rgb
        )
    }
}
