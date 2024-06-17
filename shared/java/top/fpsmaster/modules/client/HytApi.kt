package top.fpsmaster.modules.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import top.fpsmaster.features.impl.utility.LevelTag
import top.fpsmaster.utils.os.HttpRequest

class HytApi {
    fun getRank(id: String): String {
        try {
            var json: String? = null
            if (LevelTag.levelMode.mode == 1) {
                json = HttpRequest.get("https://mc-api.16163.com/search/bedwars.html?uid=$id")
            } else if (LevelTag.levelMode.mode == 2) {
                json = HttpRequest.get("https://mc-api.16163.com/search/bedwarsxp.html?uid=$id")
            } else if (LevelTag.levelMode.mode == 3) {
                json = HttpRequest.get("https://mc-api.16163.com/search/skywars.html?uid=$id")
            } else if (LevelTag.levelMode.mode == 4) {
                json = HttpRequest.get("https://mc-api.16163.com/search/kitbattle.html?uid=$id")
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
