package top.fpsmaster.features.impl.interfaces

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventAttack
import top.fpsmaster.event.events.EventRender3D
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.ModeSetting
import top.fpsmaster.interfaces.ProviderManager
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

class TargetDisplay : InterfaceModule("TargetDisplay", Category.Interface) {
    private var targetESP = ModeSetting("TargetESP", 0, "glow", "none")
    private var espColor = ColorSetting("EspColor", Color(255, 255, 255, 255)) { !targetESP.isMode("none") }

    init {
        addSettings(targetESP, targetHUD, espColor)
    }

    @Subscribe
    fun onRender(e: EventRender3D?) {
        if (target != null && target!!.health > 0 && target!!.isEntityAlive && System.currentTimeMillis() - lastHit < 3000) {
            if (targetESP.mode == 0) {
                drawCircle(target, 0.55, true)
            }
        }
    }

    private fun drawCircle(entity: Entity?, rad: Double, shade: Boolean) {
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST)
        GlStateManager.depthMask(false)
        GlStateManager.disableLighting()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f)
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH)
        GlStateManager.disableCull()
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
        val x =
            entity!!.lastTickPosX + (entity.posX - entity.lastTickPosX) * ProviderManager.timerProvider.getRenderPartialTicks() - ProviderManager.renderManagerProvider.renderPosX()
        val y =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ProviderManager.timerProvider.getRenderPartialTicks() - ProviderManager.renderManagerProvider.renderPosY() + sin(
                System.currentTimeMillis() / 2E+2
            ) + 1
        val z =
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ProviderManager.timerProvider.getRenderPartialTicks() - ProviderManager.renderManagerProvider.renderPosZ()
        var c: Color
        GL11.glColor4f(
            1f,
            0f,
            0f,
            0f
        )
        var i = 0f
        while (i < Math.PI * 2) {
            val vecX = x + rad * cos(i.toDouble())
            val vecZ = z + rad * sin(i.toDouble())
            c = espColor.color
            if (shade) {
                GL11.glColor4f(
                    c.red / 255f,
                    c.green / 255f,
                    c.blue / 255f,
                    0f
                )
                GL11.glVertex3d(vecX, y - cos(System.currentTimeMillis() / 2E+2) / 2.0f, vecZ)
                GL11.glColor4f(
                    c.red / 255f,
                    c.green / 255f,
                    c.blue / 255f,
                    0.75f
                )
            }
            GL11.glVertex3d(vecX, y, vecZ)
            i += (Math.PI * 2 / 64f).toFloat()
        }
        GL11.glEnd()
        if (shade) GL11.glShadeModel(GL11.GL_FLAT)
        GlStateManager.depthMask(true)
        GlStateManager.enableLighting()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        GlStateManager.enableCull()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glPopMatrix()
        GL11.glColor3f(1f, 1f, 1f)
    }

    @Subscribe
    fun onAttack(e: EventAttack) {
        if (e.target is EntityPlayer) {
            target = e.target as EntityPlayer
            lastHit = System.currentTimeMillis()
        }
    }

    companion object {
        @JvmField
        var targetHUD = ModeSetting("TargetHUD", 0, "simple", "none")

        @JvmField
        var target: EntityPlayer? = null

        @JvmField
        var lastHit: Long = 0
    }
}
