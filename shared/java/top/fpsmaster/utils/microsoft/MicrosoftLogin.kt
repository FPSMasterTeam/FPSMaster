package top.fpsmaster.utils.microsoft

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import net.minecraft.util.Session
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import top.fpsmaster.ui.screens.account.GuiWaiting
import top.fpsmaster.interfaces.ProviderManager
import java.awt.Desktop
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean

object MicrosoftLogin {
    private const val CLIENT_ID = "d1ed1b72-9f7c-41bc-9702-365d2cbd2e38"
    private var httpServer: HttpServer? = null
    fun start() {
        try {
            val gson = GsonBuilder().create()
            httpServer = HttpServer.create(InetSocketAddress(17342), 0)
            println("create login server")
            httpServer!!.createContext("/") { exchange: HttpExchange ->
                val map: MutableMap<String, String> = HashMap()
                val s1 = exchange.requestURI.toString()
                val result = "Login successfully! You can close this window now."
                exchange.sendResponseHeaders(200, result.length.toLong())
                val responseBody = exchange.responseBody
                responseBody.write(result.toByteArray(StandardCharsets.UTF_8))
                httpServer!!.stop(3)
                //                Hanabi.INSTANCE.notificationsManager.add(new Info("Trying to login...", Notification.Type.Info));
                val code = s1.substring(s1.indexOf("=") + 1)
                map["client_id"] = CLIENT_ID
                map["code"] = code
                map["grant_type"] = "authorization_code"
                map["redirect_uri"] = "http://127.0.0.1:17342"
                val oauth = postMAP("https://login.live.com/oauth20_token.srf", map)
                val access_token = gson.fromJson(oauth, JsonObject::class.java)["access_token"].asString
                map.clear()
                val map2: MutableMap<String, Any> = HashMap()
                map2["AuthMethod"] = "RPS"
                map2["SiteName"] = "user.auth.xboxlive.com"
                map2["RpsTicket"] = "d=$access_token"
                val jo = JsonObject()
                jo.add("Properties", gson.toJsonTree(map2))
                jo.addProperty("RelyingParty", "http://auth.xboxlive.com")
                jo.addProperty("TokenType", "JWT")
                val s2 = postJSON("https://user.auth.xboxlive.com/user/authenticate", jo)
                val jsonObject = gson.fromJson(s2, JsonObject::class.java)
                val xblToken = jsonObject["Token"].asString
                jsonObject["DisplayClaims"].getAsJsonObject()["xui"].getAsJsonArray()[0].getAsJsonObject()["uhs"].asString
                val jo2 = JsonObject()
                val jop = JsonObject()
                jop.addProperty("SandboxId", "RETAIL")
                jop.add("UserTokens", gson.toJsonTree(arrayOf(xblToken)))
                jo2.add("Properties", jop)
                jo2.addProperty("RelyingParty", "rp://api.minecraftservices.com/")
                jo2.addProperty("TokenType", "JWT")
                val xsts = postJSON("https://xsts.auth.xboxlive.com/xsts/authorize", jo2)
                val xsts_token = gson.fromJson(xsts, JsonObject::class.java)["Token"].asString
                val xsts_userhash = gson.fromJson(
                    xsts,
                    JsonObject::class.java
                )["DisplayClaims"].getAsJsonObject()["xui"].getAsJsonArray()[0].getAsJsonObject()["uhs"].asString
                val properties = JsonObject()
                properties.addProperty("identityToken", "XBL3.0 x=$xsts_userhash;$xsts_token")
                val minecraftAuth =
                    postJSON("https://api.minecraftservices.com/authentication/login_with_xbox", properties)
                val json = gson.fromJson(minecraftAuth, JsonObject::class.java)
                val accessToken = json["access_token"].asString

                // get profile
                val map3: MutableMap<String, String> = HashMap()
                map3["Authorization"] = "Bearer $accessToken"
                val profile = MicrosoftLogin["https://api.minecraftservices.com/minecraft/profile", map3]
                val profileJson = gson.fromJson(profile, JsonObject::class.java)
                val uuid2 = profileJson["id"].asString
                val name = profileJson["name"].asString
                ProviderManager.mcProvider.setSession(Session(name, uuid2, accessToken, "mojang"))
                println("logged in as $name")
                GuiWaiting.logged = true
            }
            httpServer!!.executor = null
            httpServer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun login(): Boolean {
        val flag = AtomicBoolean(false)
        try {
            val map: MutableMap<String, String> = HashMap()
            map["client_id"] = CLIENT_ID
            map["response_type"] = "code"
            map["redirect_uri"] = "http://127.0.0.1:17342"
            map["scope"] = "XboxLive.signin%20XboxLive.offline_access"
            val s = buildUrl(
                "https://login.live.com/oauth20_authorize.srf",
                map
            )
            start()
            Desktop.getDesktop().browse(URI.create(s))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return flag.get()
    }

    fun postMAP(url: String?, param: Map<String, String>?): String {
        // 创建Httpclient对象
        val httpClient = HttpClients.createDefault()
        var response: CloseableHttpResponse? = null
        var resultString = ""
        try {
            // 创建Http Post请求
            val httpPost = HttpPost(url)
            // 创建参数列表
            if (param != null) {
                val paramList: MutableList<NameValuePair> = ArrayList()
                for (key in param.keys) {
                    paramList.add(BasicNameValuePair(key, param[key]))
                }
                // 模拟表单
                val entity = UrlEncodedFormEntity(paramList)
                httpPost.entity = entity
            }
            // 执行http请求
            response = httpClient.execute(httpPost)
            resultString = EntityUtils.toString(response.entity, "utf-8")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                response?.close()
                httpClient.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return resultString
    }

    operator fun get(s: String?, headers: Map<String, String>): String {
        var httpResponse: CloseableHttpResponse? = null
        var result = ""
        // 创建httpClient实例
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        // 创建httpPost远程连接实例
        val httpGet = HttpGet(s)
        // 配置请求参数实例
        val requestConfig = RequestConfig.custom().setConnectTimeout(35000) // 设置连接主机服务超时时间
            .setConnectionRequestTimeout(35000) // 设置连接请求超时时间
            .setSocketTimeout(60000) // 设置读取数据连接超时时间
            .build()

        // 为httpPost实例设置配置
        httpGet.config = requestConfig
        // 设置请求头
        headers.forEach { (name: String?, value: String?) -> httpGet.addHeader(name, value) }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpGet)
            // 从响应对象中获取响应内容
            val entity = httpResponse.entity
            result = EntityUtils.toString(entity)
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
                httpClient.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun buildUrl(url: String, map: Map<String, String>): String {
        val sb = StringBuilder(url)
        if (map.isNotEmpty()) {
            sb.append("?")
            for ((key, value) in map) {
                sb.append(key).append("=").append(value).append("&")
            }
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    private fun postJSON(url: String?, jsonObject: JsonObject): String {
        var httpResponse: CloseableHttpResponse? = null
        var result = ""
        // 创建httpClient实例
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        // 创建httpPost远程连接实例
        val httpPost = HttpPost(url)
        // 配置请求参数实例
        val requestConfig = RequestConfig.custom().setConnectTimeout(35000) // 设置连接主机服务超时时间
            .setConnectionRequestTimeout(35000) // 设置连接请求超时时间
            .setSocketTimeout(60000) // 设置读取数据连接超时时间
            .build()
        httpPost.addHeader("Content-Type", "application/json")
        httpPost.addHeader("Accept", "application/json")

        // 为httpPost实例设置配置
        httpPost.config = requestConfig
        // 设置请求头
        // 封装post请求参数
        try {
            val s = StringEntity(jsonObject.toString())
            httpPost.entity = s
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost)
            // 从响应对象中获取响应内容
            val entity = httpResponse.entity
            result = EntityUtils.toString(entity)
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // 关闭资源
            httpResponse?.close()
            httpClient.close()
        }
        return result
    }
}
