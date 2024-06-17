package top.fpsmaster.features.settings.impl

import top.fpsmaster.features.settings.Setting

class BooleanSetting(name: String, value: Boolean) : Setting<Boolean>(name, value) {
    constructor(name: String, value: Boolean, visible: () -> Boolean) : this(name, value) {
        this.visible = visible
    }
    fun toggle() {
        value = !value
    }
}
