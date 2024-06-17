package top.fpsmaster.features.impl.optimizes

import net.minecraft.world.World
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.wrapper.mods.WrapperPerformance

class Performance : Module("Performance", Category.OPTIMIZE) {
    init {
        addSettings(entitiesOptimize, fastLoad, ignoreStands, entityLimit, fpsLimit, particlesLimit, blur)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    companion object {
        @JvmField
        var using = false
        @JvmField
        var ignoreStands = BooleanSetting("IgnoreStands", true)
        @JvmField
        var entitiesOptimize = BooleanSetting("EntitiesOptimize", true)
        @JvmField
        var fastLoad = BooleanSetting("FastLoad", true)
        @JvmField
        var fpsLimit = NumberSetting("FPSLimit", 30, 0, 360, 1)
        @JvmField
        var entityLimit = NumberSetting("EntityLimit", 200, 0, 800, 1)
        @JvmField
        var particlesLimit = NumberSetting("ParticlesLimit", 100, 0, 2000, 1)
        var blur = BooleanSetting("Blur", false)
        @JvmStatic
        fun isVisible(entity: CheckEntity): Boolean {
            return WrapperPerformance.isVisible(entity)
        }

        fun isVisible(
            world: World?,
            minX: Double,
            minY: Double,
            minZ: Double,
            maxX: Double,
            maxY: Double,
            maxZ: Double,
            cameraX: Double,
            cameraY: Double,
            cameraZ: Double
        ): Boolean {
            return WrapperPerformance.isVisible(world, minX, minY, minZ, maxX, maxY, maxZ, cameraX, cameraY, cameraZ)
        }
    }
}
