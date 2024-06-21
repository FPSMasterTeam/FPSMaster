package top.fpsmaster.features.impl.render

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventMotionBlur
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.OptifineUtil
import top.fpsmaster.utils.Utility
import top.fpsmaster.wrapper.renderEngine.bufferbuilder.WrapperBufferBuilder

class MotionBlur : Module("MotionBlur", Category.RENDER) {
    private var blurBufferMain: Framebuffer? = null
    private var blurBufferInto: Framebuffer? = null
    private var multiplier = NumberSetting("Multiplier", 2, 0, 10, 0.5)

    init {
        addSettings(multiplier)
    }

    override fun onEnable() {
        super.onEnable()
        if (OptifineUtil.isFastRender()) {
            OptifineUtil.setFastRender(false)
        }
    }

    private fun checkFramebufferSizes(framebuffer: Framebuffer?, width: Int, height: Int): Framebuffer {
        var framebuffer = framebuffer
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer == null) {
                framebuffer = Framebuffer(width, height, true)
            } else {
                framebuffer.createBindFramebuffer(width, height)
            }
            framebuffer.setFramebufferFilter(9728)
        }
        return framebuffer
    }

    private fun drawTexturedRectNoBlend(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        uMin: Float,
        uMax: Float,
        vMin: Float,
        vMax: Float,
        filter: Int
    ) {
        GlStateManager.enableTexture2D()
        GL11.glTexParameteri(3553, 10241, filter)
        GL11.glTexParameteri(3553, 10240, filter)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = WrapperBufferBuilder(tessellator)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x, y + height, 0.0).tex(uMin.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer.pos(x + width, y + height, 0.0).tex(uMax.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer.pos(x + width, y, 0.0).tex(uMax.toDouble(), vMin.toDouble()).endVertex()
        worldrenderer.pos(x, y, 0.0).tex(uMin.toDouble(), vMin.toDouble()).endVertex()
        tessellator.draw()
        GL11.glTexParameteri(3553, 10241, 9728)
        GL11.glTexParameteri(3553, 10240, 9728)
    }

    @Subscribe
    fun renderOverlay(event: EventMotionBlur?) {
        if (ProviderManager.mcProvider.getPlayer() == null || ProviderManager.mcProvider.getPlayer()!!.ticksExisted < 20) return
        if (mc.currentScreen == null) {
            if (OpenGlHelper.isFramebufferEnabled()) {
                val sr = ScaledResolution(Minecraft.getMinecraft())
                val width = mc.framebuffer.framebufferWidth
                val height = mc.framebuffer.framebufferHeight
                GlStateManager.matrixMode(5889)
                GlStateManager.loadIdentity()
                GlStateManager.ortho(
                    0.0,
                    (width / sr.scaleFactor).toDouble(),
                    (height / sr.scaleFactor).toDouble(),
                    0.0,
                    2000.0,
                    4000.0
                )
                GlStateManager.matrixMode(5888)
                GlStateManager.loadIdentity()
                GlStateManager.translate(0f, 0f, -2000f)
                blurBufferMain = checkFramebufferSizes(blurBufferMain, width, height)
                blurBufferInto = checkFramebufferSizes(blurBufferInto, width, height)
                blurBufferInto!!.framebufferClear()
                blurBufferInto!!.bindFramebuffer(true)
                OpenGlHelper.glBlendFunc(770, 771, 0, 1)
                GlStateManager.disableLighting()
                GlStateManager.disableFog()
                GlStateManager.disableBlend()
                mc.framebuffer.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, 1f)
                drawTexturedRectNoBlend(
                    0.0f,
                    0.0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0.0f,
                    1.0f,
                    0.0f,
                    1.0f,
                    9728
                )
                GlStateManager.enableBlend()
                blurBufferMain!!.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, multiplier.value.toFloat() / 10 - 0.1f)
                drawTexturedRectNoBlend(
                    0f,
                    0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0f,
                    1f,
                    1f,
                    0f,
                    9728
                )
                mc.framebuffer.bindFramebuffer(true)
                blurBufferInto!!.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.enableBlend()
                OpenGlHelper.glBlendFunc(770, 771, 1, 771)
                drawTexturedRectNoBlend(
                    0.0f,
                    0.0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0.0f,
                    1.0f,
                    0.0f,
                    1.0f,
                    9728
                )
                val tempBuff = blurBufferMain
                blurBufferMain = blurBufferInto
                blurBufferInto = tempBuff
            }
        }
    }
}
