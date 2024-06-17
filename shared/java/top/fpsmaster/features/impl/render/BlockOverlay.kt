package top.fpsmaster.features.impl.render

import net.minecraft.block.BlockStairs
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventRender3D
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.utils.render.Render3DUtils
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.blockpos.WrapperBlockPos
import top.fpsmaster.wrapper.util.WrapperAxisAlignedBB
import java.awt.Color

class BlockOverlay : Module("BlockOverlay", Category.RENDER) {
    var fill = BooleanSetting("Fill", true)
    var outline = BooleanSetting("Outline", true)
    var throughBlock = BooleanSetting("ThroughBlock", false)
    var width = NumberSetting("Width", 1, 0.1, 10, 0.1){outline.value}
    var color1 = ColorSetting("FillColor", Color(255, 255, 255, 50)){fill.value}
    var color2 = ColorSetting("OutlineColor", Color(255, 255, 255, 255)){outline.value}

    init {
        addSettings(fill, color1, outline, width, color2, throughBlock)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    @Subscribe
    fun onRender3D(e: EventRender3D?) {
        if (mc.objectMouseOver != null) {
            if (ProviderManager.mcProvider.isHoveringOverBlock()) {
                val pos = WrapperBlockPos(mc.objectMouseOver.blockPos)
                val state = ProviderManager.worldClientProvider.getBlockState(pos)
                val block =  ProviderManager.worldClientProvider.getBlock(pos)
                val x = pos.x - mc.renderManager.viewerPosX
                val y = pos.y - mc.renderManager.viewerPosY
                val z = pos.z - mc.renderManager.viewerPosZ
                GL11.glPushMatrix()
                GlStateManager.enableAlpha()
                GlStateManager.enableBlend()
                GL11.glBlendFunc(770, 771)
                GL11.glDisable(3553)
                GL11.glEnable(2848)
                if (throughBlock.value) {
                    GL11.glDisable(2929)
                }
                GL11.glDepthMask(false)
                val blockBoundingBox =  ProviderManager.worldClientProvider.getBlockBoundingBox(pos, state)
                val minX: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minX()
                val minY: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minY()
                val minZ: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minZ()
                if (fill.value) {
                    val color = color1.value.color
                    GL11.glPushMatrix()
                    GlStateManager.color(
                        color.red / 255.0f,
                        color.green / 255.0f,
                        color.blue / 255.0f,
                        color.alpha / 255.0f
                    )
                    Render3DUtils.drawBoundingBox(
                        WrapperAxisAlignedBB(
                            x + minX - 0.01,
                            y + minY - 0.01,
                            z + minZ - 0.01,
                            x + blockBoundingBox.maxX() + 0.01,
                            y + blockBoundingBox.maxY() + 0.01,
                            z + blockBoundingBox.maxZ() + 0.01
                        )
                    )
                    GL11.glPopMatrix()
                }
                if (outline.value) {
                    val color = color2.value.color
                    GL11.glPushMatrix()
                    GlStateManager.color(
                        color.red / 255.0f,
                        color.green / 255.0f,
                        color.blue / 255.0f,
                        color.alpha / 255.0f
                    )
                    GL11.glLineWidth(width.value.toFloat())
                    Render3DUtils.drawBoundingBoxOutline(
                        WrapperAxisAlignedBB(
                            x + minX - 0.005,
                            y + minY - 0.005,
                            z + minZ - 0.005,
                            x + blockBoundingBox.maxX() + 0.005,
                            y + blockBoundingBox.maxY() + 0.005,
                            z + blockBoundingBox.maxZ() + 0.005
                        )
                    )
                    GL11.glPopMatrix()
                }
                GL11.glDisable(2848)
                GL11.glEnable(3553)
                if (throughBlock.value) {
                    GL11.glEnable(2929)
                }
                GL11.glDepthMask(true)
                GL11.glLineWidth(1.0f)
                GL11.glPopMatrix()
            }
        }
    }

    companion object {
        var using = false
    }
}
