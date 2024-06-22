package top.fpsmaster.modules.config

class Configure {
    var configures = HashMap<String, String>()
    fun getOrCreate(name: String, defaultValue: String): String {
        return if (configures.containsKey(name)) {
            configures[name]?:defaultValue
        } else {
            configures[name] = defaultValue
            defaultValue
        }
    }

    operator fun set(name: String, value: String) {
        configures[name] = value
    }
}
