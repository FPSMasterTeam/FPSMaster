package top.fpsmaster.features.impl.render

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender2D
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.render.Render2DUtils
import top.fpsmaster.interfaces.ProviderManager
import java.awt.Color

class Crosshair : Module("Crosshair", Category.RENDER) {
    private var dynamic = NumberSetting("Dynamic", 4, 0, 10, 0.1)
    private var outline = BooleanSetting("Outline", true)
    private var outlineWidth = NumberSetting("OutlineWidth", 1, 0, 10, 0.1) { outline.value }
    private var dot = BooleanSetting("Dot", true)
    private var gap = NumberSetting("Gap", 6, 0, 10, 0.1)
    private var width = NumberSetting("Width", 0.6, 0, 10, 0.1)
    private var length = NumberSetting("Length", 3.5, 0, 10, 0.1)
    private var color = ColorSetting("Color", Color(255, 255, 255))
    private var outlineColor = ColorSetting("OutlineColor", Color(161, 161, 161)) { outline.value }
    private var enemyColor = ColorSetting("Enemy", Color(255, 55, 50))
    private var friendColor = ColorSetting("Friend", Color(20, 255, 55))

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    private var dyna = 0f

    init {
        addSettings(dynamic, outline, outlineColor, outlineWidth, gap, width, length, color, enemyColor, friendColor)
    }

    @Subscribe
    fun onRender(e: EventRender2D) {
        val sr = ScaledResolution(mc)
        val gap = gap.value.toFloat() + dyna
        val lineWidth = width.value.toFloat()
        val length = length.value.toFloat()
        val dynamic = dynamic.value.toFloat()
        val outline = outline.value
        val dot = dot.value
        dyna = if (isMoving) {
            base(dyna.toDouble(), dynamic.toDouble(), 0.2).toFloat()
        } else {
            base(dyna.toDouble(), 0.0, 0.2).toFloat()
        }
        var col = color.color
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            if (mc.objectMouseOver.entityHit is EntityPlayer && isTeammate(mc.objectMouseOver.entityHit as EntityPlayer) || mc.objectMouseOver.entityHit is EntityAnimal) {
                col = friendColor.color
            }
            if (mc.objectMouseOver.entityHit is EntityPlayer && !isTeammate(mc.objectMouseOver.entityHit as EntityPlayer) || mc.objectMouseOver.entityHit is EntityMob) {
                col = enemyColor.color
            }
        }

        // |
        drawOutlineRect(
            sr.scaledWidth / 2f - lineWidth / 2f,
            sr.scaledHeight / 2f - length - gap,
            lineWidth,
            length,
            if (outline) outlineWidth.value.toFloat() else 0f,
            col
        )
        drawOutlineRect(
            sr.scaledWidth / 2f - lineWidth / 2f,
            sr.scaledHeight / 2f + gap,
            lineWidth,
            length,
            if (outline) outlineWidth.value.toFloat() else 0f,
            col
        )
        // -
        drawOutlineRect(
            sr.scaledWidth / 2f - length - gap,
            sr.scaledHeight / 2f - lineWidth / 2f,
            length,
            lineWidth,
            if (outline) outlineWidth.value.toFloat() else 0f,
            col
        )
        drawOutlineRect(
            sr.scaledWidth / 2f + gap,
            sr.scaledHeight / 2f - lineWidth / 2f,
            length,
            lineWidth,
            if (outline) outlineWidth.value.toFloat() else 0f,
            col
        )
        // dot
        if (dot) {
            if (outline) Render2DUtils.drawRect(
                sr.scaledWidth / 2f - 1 - outlineWidth.value.toFloat(),
                sr.scaledHeight / 2f - 1 - outlineWidth.value.toFloat(),
                2 + outlineWidth.value.toFloat() * 2,
                2 + outlineWidth.value.toFloat() * 2,
                outlineColor.color
            )
            Render2DUtils.drawRect(sr.scaledWidth / 2f - 1, sr.scaledHeight / 2f - 1, 2f, 2f, col)
        }
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }

    private fun drawOutlineRect(x: Float, y: Float, width: Float, height: Float, outlineWidth: Float, color: Color?) {
        Render2DUtils.drawRect(
            x - outlineWidth,
            y - outlineWidth,
            width + outlineWidth * 2,
            height + outlineWidth * 2,
            outlineColor.color
        )
        Render2DUtils.drawRect(x, y, width, height, color)
    }

    private fun isTeammate(e: EntityPlayer): Boolean {
        val player = ProviderManager.mcProvider.getPlayer() ?: return false
        val team = e.team ?: return false
        return team.isSameTeam(player.team)
    }

    private val isMoving: Boolean
        get() = ProviderManager.mcProvider.getPlayer()!!.isSprinting

    companion object {
        @JvmField
        var using = false
    }
}
