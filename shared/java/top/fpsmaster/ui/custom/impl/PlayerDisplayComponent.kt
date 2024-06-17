package top.fpsmaster.ui.custom.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.PlayerDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.utils.render.Render2DUtils
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.WorldClientProvider
import java.awt.Color

class PlayerDisplayComponent : Component(PlayerDisplay::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        width = 40f
        var i = 0
        for (entity in ProviderManager.worldClientProvider.getWorld()!!.playerEntities) {
            if (entity != null) {
                if (i > 10 || entity === ProviderManager.mcProvider.getPlayer()) continue
                val s16 = FPSMaster.INSTANCE.fontManager!!.s16
                val hX = s16.getStringWidth((entity.health * 10 / 10).toInt().toString() + " hp").toFloat()
                val nX = s16.getStringWidth(entity.displayName.formattedText).toFloat()
                Render2DUtils.drawOptimizedRoundedRect(x, y + i * 16, 10 + hX + nX, 14f, Color(0, 0, 0, 60))
                Render2DUtils.drawOptimizedRoundedRect(
                    x,
                    y + i * 16,
                    (10 + hX + nX) * entity.health / entity.maxHealth,
                    14f,
                    Color(0, 0, 0, 60)
                )
                if (width < 10 + hX + nX) width = 10 + hX + nX
                val health = entity.health
                val maxHealth = entity.maxHealth
                var color: Color
                color = if (health >= maxHealth * 0.8) {
                    Color(50, 255, 55)
                } else if (health > maxHealth * 0.5) {
                    Color(255, 255, 55)
                } else {
                    Color(255, 55, 55)
                }
                s16.drawString(entity.displayName.formattedText, x + 2, y + i * 16 + 2, -1)
                s16.drawString(
                    (entity.health * 10 / 10).toInt().toString() + " hp", x + 8 + nX, y + i * 16 + 2, color.rgb
                )
                i++
            }
        }
        height = (18 * i).toFloat()
    }
}
