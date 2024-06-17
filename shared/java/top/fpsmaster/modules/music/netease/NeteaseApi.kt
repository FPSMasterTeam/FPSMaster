package top.fpsmaster.modules.music.netease

import top.fpsmaster.utils.os.HttpRequest.getWithCookie

object NeteaseApi {
    private const val BASE_URL = "https://music.skidder.top/"
    @JvmField
    var cookies = ""
    @JvmStatic
    fun getVerbatimLyrics(id: String): String {
        val url = BASE_URL + "lyric/new?id=" + id
        return getWithCookie(url, cookies)
    }

    @JvmStatic
    fun searchSongs(keywords: String): String {
        val url = BASE_URL + "cloudsearch?keywords=" + keywords
        return getWithCookie(url, cookies)
    }

    fun checkMusic(id: String): String {
        val url = BASE_URL + "check/music?id=" + id
        return getWithCookie(url, cookies)
    }

    val userData: String
        get() {
            val url = BASE_URL + "user/level"
            return getWithCookie(url, cookies)
        }

    @JvmStatic
    fun getPlayURL(id: String): String {
        val url = BASE_URL + "song/url/v1?id=" + id + "&level=higher"
        return getWithCookie(url, cookies)
    }

    @JvmStatic
    fun getPlayList(id: String): String {
        val url = BASE_URL + "playlist/track/all?id=" + id + "&limit=50"
        return getWithCookie(url, cookies)
    }

    @JvmStatic
    val dailyList: String
        get() {
            val url = BASE_URL + "recommend/songs"
            return getWithCookie(url, cookies)
        }
    @JvmStatic
    val uniKey: String
        get() {
            val url = BASE_URL + "login/qr/key" + "?timestamp=" + System.currentTimeMillis()
            return getWithCookie(url, "")
        }

    @JvmStatic
    fun generateQRCode(key: String): String {
        val url = BASE_URL + "login/qr/create?key=" + key + "&qrimg=true" + "&timestamp=" + System.currentTimeMillis()
        return getWithCookie(url, "")
    }

    @JvmStatic
    fun checkLoginStatus(key: String): String {
        val url = BASE_URL + "login/qr/check?key=" + key + "&timestamp=" + System.currentTimeMillis()
        return getWithCookie(url, "")
    }

    @JvmStatic
    val anonmous: String
        get() {
            val url = BASE_URL + "/register/anonimous" + "&timestamp=" + System.currentTimeMillis()
            return getWithCookie(url, "")
        }
}
