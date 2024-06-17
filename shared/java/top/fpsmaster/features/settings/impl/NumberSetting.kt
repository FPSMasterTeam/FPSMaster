package top.fpsmaster.features.settings.impl

import top.fpsmaster.features.settings.Setting
import kotlin.math.max
import kotlin.math.min

class NumberSetting(
    name: String,
    value: Number,
    var min: Number,
    var max: Number,
    var inc: Number
) :
    Setting<Number>(name, value) {

    constructor(
        name: String,
        value: Number,
        min: Number,
        max: Number,
        inc: Number,
        visible: () -> Boolean
    ) : this(name, value, min, max, inc) {
        this.visible = visible
    }

override var value: Number = value
    set(newValue) {
        var closestMultipleOfInc = Math.round(newValue.toDouble() / inc.toDouble()) * inc.toDouble()
        // 保留两位小数
        closestMultipleOfInc = Math.round(closestMultipleOfInc * 100) / 100.0
        field = max(min.toDouble(), min(max.toDouble(), closestMultipleOfInc))
    }
}
