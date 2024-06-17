package top.fpsmaster.utils.math.animation

import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

class ColorAnimation {
    var r = Animation()
    var g = Animation()
    var b = Animation()
    var a = Animation()
    private var first = true
    private var end: Color? = null

    constructor()
    constructor(color: Color) {
        this.color = color
    }

    constructor(red: Int, green: Int, blue: Int, alpha: Int) {
        color = Color(red, green, blue, alpha)
    }

    fun start(start: Color, end: Color, duration: Float, type: Type) {
        this.end = end
        r.start(start.red.toDouble(), end.red.toDouble(), duration, type)
        g.start(start.green.toDouble(), end.green.toDouble(), duration, type)
        b.start(start.blue.toDouble(), end.blue.toDouble(), duration, type)
        a.start(start.alpha.toDouble(), end.alpha.toDouble(), duration, type)
    }

    fun update() {
        if (end != null) {
            if (first) {
                color = end as Color
                first = false
                return
            }
            r.update()
            g.update()
            b.update()
            a.update()
        }
    }

    fun reset() {
        r.reset()
        g.reset()
        b.reset()
        a.reset()
    }

    var color: Color
        get() = Color(
            Render2DUtils.limit(r.value),
            Render2DUtils.limit(g.value),
            Render2DUtils.limit(b.value),
            Render2DUtils.limit(a.value)
        )
        set(color) {
            r.value = color.red.toDouble()
            g.value = color.green.toDouble()
            b.value = color.blue.toDouble()
            a.value = color.alpha.toDouble()
        }

    fun fstart(color: Color, color1: Color, duration: Float, type: Type) {
        end = color1
        r.fstart(color.red.toDouble(), color1.red.toDouble(), duration, type)
        g.fstart(color.green.toDouble(), color1.green.toDouble(), duration, type)
        b.fstart(color.blue.toDouble(), color1.blue.toDouble(), duration, type)
        a.fstart(color.alpha.toDouble(), color1.alpha.toDouble(), duration, type)
    }

    fun base(color: Color) {
        r.value = AnimationUtils.base(r.value, color.red.toDouble(), 0.1)
        g.value = AnimationUtils.base(g.value, color.green.toDouble(), 0.1)
        b.value = AnimationUtils.base(b.value, color.blue.toDouble(), 0.1)
        a.value = AnimationUtils.base(a.value, color.alpha.toDouble(), 0.1)
    }
}
