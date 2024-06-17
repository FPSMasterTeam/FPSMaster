package top.fpsmaster.features.impl.interfaces

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.util.EntitySelectors
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventAttack
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.wrapper.TimerProvider
import top.fpsmaster.wrapper.util.WrapperAxisAlignedBB
import top.fpsmaster.wrapper.util.WrapperVec3
import java.awt.Color

class ReachDisplay : InterfaceModule("ReachDisplay", Category.Interface) {

    init {
        addSettings(rounded, backgroundColor, fontShadow, betterFont, textColor, bg, rounded , roundRadius)
    }

    companion object {
        @JvmField
        var reach = 0.0

        @JvmField
        var textColor = ColorSetting("TextColor", Color(255, 255, 255))
    }

    @Subscribe
    fun onAttack(e: EventAttack) {
        val entity: Entity? = mc.renderViewEntity
        if (entity != null && ProviderManager.mcProvider.getWorld() != null) {
            var d0: Double = mc.playerController.blockReachDistance.toDouble()
            val rayTrace = entity.rayTrace(d0, ProviderManager.timerProvider.getRenderPartialTicks())
            val vec3d = entity.getPositionEyes(ProviderManager.timerProvider.getRenderPartialTicks())
            var d1 = d0
            if (mc.playerController.extendedReach()) {
                d1 = 6.0
                d0 = d1
            }
            if (rayTrace != null) {
                d1 = rayTrace.hitVec.distanceTo(vec3d)
            }
            val vec3d1 = WrapperVec3(entity.getLook(1.0f))
            val vec3d2 = WrapperVec3(vec3d).addVector(vec3d1.x() * d0, vec3d1.y() * d0, vec3d1.z() * d0)
            var vec3d3:Any? = null
            if (ProviderManager.mcProvider.getWorld() == null)
                return
            val list: List<Entity> = ProviderManager.mcProvider.getWorld()!!.getEntitiesInAABBexcluding(
                entity,
                WrapperAxisAlignedBB(entity.entityBoundingBox).addCoord(vec3d1.x() * d0, vec3d1.y() * d0, vec3d1.z() * d0)
                    .expand(1.0, 1.0, 1.0).axisAlignedBB,
                Predicates.and(EntitySelectors.NOT_SPECTATING,
                    Predicate { entity1 -> entity1 != null && entity1.canBeCollidedWith() })
            )
            var d2 = d1
            for (j in list.indices) {
                val entity1 = list[j]
                val axisalignedbb = WrapperAxisAlignedBB(entity1.entityBoundingBox).expand(entity1.collisionBorderSize.toDouble())
                val raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2)
                if (axisalignedbb.isVecInside(vec3d)) {
                    if (d2 >= 0.0) {
                        vec3d3 = raytraceresult!!.hitVec
                        d2 = 0.0
                    }
                } else if (raytraceresult != null) {
                    val d3 = vec3d.distanceTo(raytraceresult.hitVec)
                    if (d3 < d2 || d2 == 0.0) {
                        vec3d3 = raytraceresult.hitVec
                    }
                }
            }

            val distance = WrapperVec3(vec3d).distanceTo(vec3d3)
            reach = (distance * 100).toInt().toDouble() / 100
        }
    }
}
