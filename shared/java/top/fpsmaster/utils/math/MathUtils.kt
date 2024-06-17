package top.fpsmaster.utils.math

import kotlin.math.max
import kotlin.math.min

object MathUtils {
    @JvmStatic
    fun clamp(cursorPosition: Int, i: Int, i1: Int): Int {
        return max(i, min(i1, cursorPosition))
    }

    fun increasedSpeed(current: Float, start: Float, target: Float, speed: Float): Float {
        if (start == target) {
            return start
        } else if (start < target) {
            if (current < start) return start
            if (current > target) return target
        } else {
            if (current > start) return start
            if (current < target) return target
        }
        val k = speed / (target - start)
        return current + (k * (current - start) + speed) * if (start > target) -1 else 1
    }

    @JvmStatic
    fun decreasedSpeed(current: Float, start: Float, target: Float, speed: Float): Float {
        val k = speed / (start - target)
        return current + (k * (current - start) + speed) * if (start > target) -1 else 1
    }
}
