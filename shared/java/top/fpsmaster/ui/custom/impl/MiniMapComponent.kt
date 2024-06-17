package top.fpsmaster.ui.custom.impl

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import top.fpsmaster.features.impl.interfaces.MiniMap
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.ui.minimap.XaeroMinimap
import top.fpsmaster.ui.minimap.animation.MinimapAnimation
import top.fpsmaster.ui.minimap.interfaces.InterfaceHandler
import top.fpsmaster.utils.render.Render2DUtils
import java.io.IOException

class MiniMapComponent : Component(MiniMap::class.java) {
    init {
        y = 0.3f
        width = 75f
        height = 75f
    }

    private var loadedMinimap = false
    private val minimap: XaeroMinimap = XaeroMinimap()

    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        if (!loadedMinimap) {
            loadedMinimap = true
            try {
                minimap.load()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        InterfaceHandler.drawInterfaces(ProviderManager.timerProvider.getRenderPartialTicks())
        Render2DUtils.drawRect(x + width / 2 - 1, y + height / 2 - 1, 2f, 2f, -1)
        MinimapAnimation.tick()
    }
}