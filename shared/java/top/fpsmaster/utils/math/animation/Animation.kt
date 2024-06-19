package top.fpsmaster.utils.math.animation

class Animation {
    private var duration: Long = 0
    private var startTime: Long = 0
    private var start = 0.0
    @JvmField
    var value = 0.0
    @JvmField
    var end = 0.0
    private var type = Type.LINEAR
    private var isStarted = false

    fun start(start: Double, end: Double, duration: Float, type: Type) {
        if (!isStarted) {
            if (start != this.start || end != this.end || (duration * 1000).toLong() != this.duration || type != this.type) {
                this.duration = (duration * 1000).toLong()
                this.start = start
                startTime = System.currentTimeMillis()
                value = start
                this.end = end
                this.type = type
                isStarted = true
            }
        }
    }

    fun update() {
        if (!isStarted) return
        val result: Double = when (type) {
            Type.LINEAR -> AnimationUtils.linear(startTime, duration, start, end)
            Type.EASE_IN_QUAD -> AnimationUtils.easeInQuad(
                startTime,
                duration,
                start,
                end
            )

            Type.EASE_OUT_QUAD -> AnimationUtils.easeOutQuad(
                startTime,
                duration,
                start,
                end
            )

            Type.EASE_IN_OUT_QUAD -> AnimationUtils.easeInOutQuad(
                startTime,
                duration,
                start,
                end
            )

            Type.EASE_IN_ELASTIC -> AnimationUtils.easeInElastic(
                (System.currentTimeMillis() - startTime).toDouble(),
                start,
                end - start,
                duration.toDouble()
            )

            Type.EASE_OUT_ELASTIC -> AnimationUtils.easeOutElastic(
                (System.currentTimeMillis() - startTime).toDouble(),
                start,
                end - start,
                duration.toDouble()
            )

            Type.EASE_IN_OUT_ELASTIC -> AnimationUtils.easeInOutElastic(
                (System.currentTimeMillis() - startTime).toDouble(),
                start,
                end - start,
                duration.toDouble()
            )

            Type.EASE_IN_BACK -> AnimationUtils.easeInBack(
                (System.currentTimeMillis() - startTime).toDouble(),
                start,
                end - start,
                duration.toDouble()
            )

            Type.EASE_OUT_BACK -> AnimationUtils.easeOutBack(
                (System.currentTimeMillis() - startTime).toDouble(),
                start,
                end - start,
                duration.toDouble()
            )
        }
        value = result
        if (System.currentTimeMillis() - startTime > duration) {
            isStarted = false
            value = end
        }
    }

    fun reset() {
        value = 0.0
        start = 0.0
        end = 0.0
        startTime = System.currentTimeMillis()
        isStarted = false
    }

    fun fstart(start: Double, end: Double, duration: Float, type: Type) {
        isStarted = false
        start(start, end, duration, type)
    }
}
