package top.fpsmaster.features.settings.impl

import top.fpsmaster.features.settings.Setting

class ModeSetting(name: String, value: Int, vararg modes: String) : Setting<Int>(name, value) {

    constructor(name: String, value: Int, vararg modes: String,visible: ()->Boolean) : this(name, value, *modes) {
        this.visible = visible
    }

    private var modes: Array<String> = modes as Array<String>

    fun cycle() {
        value = (value + 1) % modes.size
    }

    fun getMode(num: Int): String {
        return modes[num - 1]
    }

    fun isMode(mode: String): Boolean {
        return modes[value] == mode
    }

    val modeName: String
        get() = modes[value]
    val mode: Int
        get() = value
    val modesSize: Int
        get() = modes.size
}
