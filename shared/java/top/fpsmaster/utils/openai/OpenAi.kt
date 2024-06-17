package top.fpsmaster.utils.openai

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import top.fpsmaster.FPSMaster
import top.fpsmaster.modules.logger.Logger
import top.fpsmaster.utils.os.HttpRequest


fun requestClientAI(prompt: String, model: String, messages: JsonArray): Array<String?> {
    try {
        val sendPostRequest = HttpRequest.sendPostRequest(
            "${FPSMaster.SERVICE_API}/chat?timestamp=${System.currentTimeMillis()}", Gson().toJson(messages), hashMapOf(
                "Content-Type" to "application/json",
                "username" to FPSMaster.INSTANCE.accountManager!!.username,
                "token" to FPSMaster.INSTANCE.accountManager!!.token,
                "model" to model,
                "prompt" to prompt
            )
        )
        if (sendPostRequest.size == 2) {
            val s = sendPostRequest[1]
            val json = JsonParser().parse(s).asJsonObject
            return if ("200" == sendPostRequest[0]) {
                arrayOf(json["code"].asString, json["data"].asString)
            }else{
                arrayOf("501", "Server went wrong")
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
        return arrayOf("500", e.toString())
    }
    return arrayOf("500", "Request failed")
}

class OpenAi(
    private val baseUrl: String,
    private val openAiKey: String,
    private val model: String,
    private val prompt: String
) {

    fun requestNewAnswer(question: String?, msgs: JsonArray): String {
        val systemRole = JsonObject()
        systemRole.addProperty("role", "system")
        systemRole.addProperty("content", prompt)
        val messages = JsonArray()
        messages.add(systemRole)
        messages.addAll(msgs)
        val body = JsonObject()
        body.addProperty("model", model)
        body.add("messages", messages)
        val json = body.toString()
        val hashMap: MutableMap<String, String> = HashMap()
        hashMap["Content-Type"] = "application/json"
        hashMap["Authorization"] = "Bearer $openAiKey"
        val text: Array<String?> = try {
            HttpRequest.sendPostRequest("$baseUrl/chat/completions", json, hashMap)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        if (text.size != 2) return "failed"
        val responseJson = JsonParser().parse(text[1]).getAsJsonObject()
        return if ("200" == text[0]) {
            responseJson["choices"]
                .getAsJsonArray()[0]
                .getAsJsonObject()["message"]
                .getAsJsonObject()["content"]
                .asString
        } else {
            val errorJson = responseJson["error"].getAsJsonObject()
            throw RuntimeException("OpenAI returned an error: " + errorJson["message"].asString)
        }
    }

    fun requestNewAnswer(question: String?): String {
        val systemRole = JsonObject()
        systemRole.addProperty("role", "system")
        systemRole.addProperty("content", prompt)
        val userRole = JsonObject()
        userRole.addProperty("role", "user")
        userRole.addProperty("content", question)
        val messages = JsonArray()
        messages.add(systemRole)
        messages.add(userRole)
        val body = JsonObject()
        body.addProperty("model", model)
        body.add("messages", messages)
        val json = body.toString()
        val hashMap: MutableMap<String, String> = HashMap()
        hashMap["Content-Type"] = "application/json"
        hashMap["Authorization"] = "Bearer $openAiKey"
        val text: Array<String?> = try {
            HttpRequest.sendPostRequest("$baseUrl/chat/completions", json, hashMap)
        } catch (e: Exception) {
            Logger.error("Translator", e.toString())
            emptyArray()
        }
        if (text.size != 2) return "failed"
        val responseJson = JsonParser().parse(text[1]).getAsJsonObject()
        return if ("200" == text[0]) {
            responseJson["choices"]
                .getAsJsonArray()[0]
                .getAsJsonObject()["message"]
                .getAsJsonObject()["content"]
                .asString
        } else {
            val errorJson = responseJson["error"].getAsJsonObject()
            throw RuntimeException("OpenAI returned an error: " + errorJson["message"].asString)
        }
    }
}
