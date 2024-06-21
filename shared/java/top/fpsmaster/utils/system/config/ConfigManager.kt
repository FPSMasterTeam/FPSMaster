package top.fpsmaster.utils.system.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.optimizes.OldAnimations
import top.fpsmaster.features.impl.optimizes.Performance
import top.fpsmaster.features.impl.render.ItemPhysics
import top.fpsmaster.features.impl.utility.ClientCommand
import top.fpsmaster.features.impl.utility.IRC
import top.fpsmaster.features.settings.impl.*
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.ui.custom.Position
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.utils.os.FileUtils.readFile
import top.fpsmaster.utils.os.FileUtils.saveFile

class ConfigManager {
    var gson = GsonBuilder().setPrettyPrinting().create()!!
    @JvmField
    var configure = Configure()

    init {
        if (!FileUtils.dir.exists()) FileUtils.dir.mkdirs()
    }

    private fun saveComponents() {
        val json = JsonObject()
        for (module in FPSMaster.componentsManager.components) {
            val component = JsonObject()
            component.addProperty("name", module.mod.name)
            component.addProperty("x", module.x)
            component.addProperty("y", module.y)
            component.addProperty("position", module.position.name)
            json.add(module.mod.name, component)
        }
        val json1 = gson.toJson(json)
        saveFile("components.json", json1)
    }

    private fun readComponents() {
        val json1 = readFile("components.json")
        if (json1.isEmpty()) return
        val json = gson.fromJson(json1, JsonObject::class.java)
        for (mod in FPSMaster.moduleManager.modules) {
            val module = json.getAsJsonObject(mod.name)
            if (module != null) {
                val component = FPSMaster.componentsManager.components.stream()
                    .filter { component1: Component -> component1.mod.name == module["name"].asString }
                    .findFirst().orElse(null)
                    ?: continue
                component.x = module["x"].asFloat
                component.y = module["y"].asFloat
                component.position = Position.valueOf(module["position"].asString)
            }
        }
    }

    fun saveConfig(name: String) {
        saveComponents()
        val json = JsonObject()
        json.addProperty("theme", FPSMaster.themeSlot)
        json.addProperty("clientConfigure", gson.toJson(configure.configures))
        for (module in FPSMaster.moduleManager.modules) {
            val moduleJson = JsonObject()
            moduleJson.addProperty("enabled", module.isEnabled)
            moduleJson.addProperty("key", module.key)
            for (setting in module.settings) {
                if (setting is ColorSetting) {
                    moduleJson.addProperty(
                        setting.name,
                        setting.value.hue.toString() + "|" + setting.value.saturation + "|" + setting.value.brightness + "|" + setting.value.alpha
                    )
                } else {
                    moduleJson.addProperty(setting.name, setting.value.toString())
                }
            }
            json.add(module.name, moduleJson)
        }
        val json1 = gson.toJson(json)
        saveFile("$name.json", json1)
    }

    fun loadConfig(name: String) {
        try {
            var s = readFile("$name.json")
            if (s.isEmpty()) {
                openDefaultModules()
                saveConfig("default")
                loadConfig(name)
                return
            }
            readComponents()
            s = readFile("$name.json")
            val json = gson.fromJson(s, JsonObject::class.java)
            FPSMaster.themeSlot = json["theme"].asString
            for (module in FPSMaster.moduleManager.modules) {
                val moduleJson = json.getAsJsonObject(module.name) ?: continue
                module.set(moduleJson["enabled"].asBoolean)
                module.key = moduleJson["key"].asInt
                for (setting in module.settings) {
                    if (moduleJson[setting.name] == null) {
                        continue
                    }
                    if (setting is BooleanSetting) {
                        setting.value = moduleJson[setting.name].toString().replace("\"".toRegex(), "").toBoolean()
                    } else if (setting is NumberSetting) {
                        setting.value = moduleJson[setting.name].toString().replace("\"".toRegex(), "").toDouble()
                    } else if (setting is ModeSetting) {
                        setting.value = moduleJson[setting.name].toString().replace("\"".toRegex(), "").toInt()
                    } else if (setting is TextSetting) {
                        setting.value = moduleJson[setting.name].toString().replace("\"".toRegex(), "")
                    } else if (setting is ColorSetting) {
                        val split = moduleJson[setting.name].toString().trim { it <= ' ' }.replace("\"".toRegex(), "")
                            .split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (split.size == 4) {
                            setting.value.setColor(
                                split[0].toFloat(),
                                split[1].toFloat(),
                                split[2].toFloat(),
                                split[3].toFloat()
                            )
                        }
                    } else if (setting is BindSetting) {
                        setting.value = moduleJson[setting.name].asInt
                    }
                }
            }
            configure.configures = gson.fromJson(json["clientConfigure"].asString, HashMap::class.java) as HashMap<String, String>
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDefaultModules() {
        FPSMaster.moduleManager.getModule(Performance::class.java).set(true)
        FPSMaster.moduleManager.getModule(OldAnimations::class.java).set(true)
        FPSMaster.moduleManager.getModule(ItemPhysics::class.java).set(true)
        FPSMaster.moduleManager.getModule(ClientCommand::class.java).set(true)
        FPSMaster.moduleManager.getModule(IRC::class.java).set(true)
    }
}
