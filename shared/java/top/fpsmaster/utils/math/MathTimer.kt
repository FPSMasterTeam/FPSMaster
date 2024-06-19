package top.fpsmaster.utils.math

class MathTimer {
    var start: Long

    init {
        start = System.currentTimeMillis()
    }

    fun delay(delay: Long): Boolean {
        if (System.currentTimeMillis() - start > delay) {
            reset()
            return true
        }
        return false
    }

    private fun reset() {
        start = System.currentTimeMillis()
    }
}
