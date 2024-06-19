package top.fpsmaster.modules.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import top.fpsmaster.features.impl.utility.LevelTag
import top.fpsmaster.utils.os.HttpRequest

class HytApi {
    fun getRank(id: String): String {
        try {
            var json: String? = null
            when (LevelTag.levelMode.mode) {
                1 -> {
                    json = HttpRequest["https://mc-api.16163.com/search/bedwars.html?uid=$id"]
                }
                2 -> {
                    json = HttpRequest["https://mc-api.16163.com/search/bedwarsxp.html?uid=$id"]
                }
                3 -> {
                    json = HttpRequest["https://mc-api.16163.com/search/skywars.html?uid=$id"]
                }
                4 -> {
                    json = HttpRequest["https://mc-api.16163.com/search/kitbattle.html?uid=$id"]
                }
            }
            return try {
                val data = Gson().fromJson(json, JsonObject::class.java)
                data["rank"].asString
            } catch (e: Exception) {
                "无"
            }
        }catch (e: Exception){
            return "无"
        }
    }
}
