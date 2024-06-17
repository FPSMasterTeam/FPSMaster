package top.fpsmaster.features.settings
open class Setting<T>(@JvmField var name: String, open var value: T) {
    constructor(name: String, value: T, visible: () -> Boolean) : this(name, value) {
        this.visible = visible
    }
    var visible: (() -> Boolean)? = null
    fun getVisible(): Boolean {
        return if (visible == null)
            true
        else
            visible!!.invoke()
    }
}
