package top.fpsmaster.features.impl.optimizes

import net.minecraft.entity.Entity

class CheckEntity(var entity: Entity) {
    val lastTimeChecked: Long = 0
    var isLastCullingVisible = false

    val minX: Double
        get() = entity.entityBoundingBox.minX
    val minY: Double
        get() = entity.entityBoundingBox.minY
    val minZ: Double
        get() = entity.entityBoundingBox.minZ
    val maxX: Double
        get() = entity.entityBoundingBox.maxX
    val maxY: Double
        get() = entity.entityBoundingBox.maxY
    val maxZ: Double
        get() = entity.entityBoundingBox.maxZ
}
