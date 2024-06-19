package top.fpsmaster.ui.custom.impl

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.TargetDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.render.Render2DUtils
import top.fpsmaster.interfaces.ProviderManager
import java.awt.Color

class TargetHUDComponent : Component(TargetDisplay::class.java) {
    var animation = 0f
    private var healthWidth = 0f
    private var colorAnimation = ColorAnimation()
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        if (TargetDisplay.targetHUD.mode != 0) return
        var target1 = TargetDisplay.target
        if (Utility.mc.ingameGUI.chatGUI.chatOpen) target1 = ProviderManager.mcProvider.getPlayer()
        if (target1 == null) return
        width = (30 + FPSMaster.INSTANCE.fontManager!!.s16.getStringWidth(target1.displayName.formattedText)).toFloat()
        height = 30f
        animation =
            if (target1.isDead || System.currentTimeMillis() - TargetDisplay.lastHit > 5000 && target1 !== ProviderManager.mcProvider.getPlayer()) {
                base(animation.toDouble(), 0.0, 0.1).toFloat()
            } else {
                base(animation.toDouble(), 80.0, 0.1).toFloat()
            }
        val health = target1.health
        val maxHealth = target1.maxHealth
        healthWidth = base(healthWidth.toDouble(), (health / maxHealth).toDouble(), 0.1).toFloat()
        if (health >= maxHealth * 0.8) {
            colorAnimation.base(Color(50, 255, 55, animation.toInt()))
        } else if (health > maxHealth * 0.5) {
            colorAnimation.base(Color(255, 255, 55, animation.toInt()))
        } else {
            colorAnimation.base(Color(255, 55, 55, animation.toInt()))
        }
        if (animation > 1) {
            Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, Color(0, 0, 0, animation.toInt()))
            Render2DUtils.drawOptimizedRoundedRect(x, y, healthWidth * width, height, colorAnimation.color)
            FPSMaster.INSTANCE.fontManager!!.s16.drawStringWithShadow(
                target1.displayName.formattedText,
                x + 27,
                y + 5,
                -1
            )
            assert(FPSMaster.INSTANCE.playerManager != null)
            FPSMaster.INSTANCE.fontManager!!.s16.drawStringWithShadow(
                FPSMaster.INSTANCE.playerManager!!.getPlayerRank(
                    target1.name
                ), x + 27, y + 15, -1
            )
            Render2DUtils.drawPlayerHead(target1, x + 5, y + 5, 20, 20)
        }
    }
}
