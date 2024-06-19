package top.fpsmaster.modules.music.netease.deserialize

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import top.fpsmaster.modules.logger.Logger.error
import top.fpsmaster.modules.music.Line
import top.fpsmaster.modules.music.Lyrics
import top.fpsmaster.modules.music.PlayList
import top.fpsmaster.modules.music.Word
import top.fpsmaster.modules.music.netease.Music
import top.fpsmaster.modules.music.netease.NeteaseApi
import top.fpsmaster.modules.music.netease.NeteaseApi.anonmous
import top.fpsmaster.modules.music.netease.NeteaseApi.checkLoginStatus
import top.fpsmaster.modules.music.netease.NeteaseApi.cookies
import top.fpsmaster.modules.music.netease.NeteaseApi.dailyList
import top.fpsmaster.modules.music.netease.NeteaseApi.generateQRCode
import top.fpsmaster.modules.music.netease.NeteaseApi.getPlayList
import top.fpsmaster.modules.music.netease.NeteaseApi.getPlayURL
import top.fpsmaster.modules.music.netease.NeteaseApi.getVerbatimLyrics
import top.fpsmaster.modules.music.netease.NeteaseApi.uniKey
import java.net.URLEncoder
import java.util.function.Consumer

object MusicWrapper {
    var gson: Gson = GsonBuilder().create()
    fun getSongUrl(id: String): String {
        val jsonObject = gson.fromJson(
            getPlayURL(
                id
            ), JsonObject::class.java
        )
        return jsonObject["data"].getAsJsonArray()[0].getAsJsonObject()["url"].asString
    }

    val qRKey: String?
        get() {
            val jsonObject = gson.fromJson(uniKey, JsonObject::class.java)
                ?: return null
            return jsonObject["data"].getAsJsonObject()["unikey"].asString
        }

    fun getQRCodeImg(key: String?): String {
        val jsonObject = gson.fromJson(
            generateQRCode(
                key!!
            ), JsonObject::class.java
        )
        val data = jsonObject["data"].getAsJsonObject()["qrimg"].asString
        return data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray<String>()[1]
    }

    fun loginAnonimous() {
        val jsonObject = gson.fromJson(anonmous, JsonObject::class.java)
        cookies = jsonObject["cookie"].asString
    }

    private fun getSongsFromList(id: String?): PlayList {
        val playList = PlayList()
        try {
            val jsonObject: JsonObject = gson.fromJson(getPlayList(id!!), JsonObject::class.java)
            val songs = jsonObject.getAsJsonArray("songs")
            for (song in songs) {
                val songObject = song.getAsJsonObject()
                val id1 = songObject["id"].asLong
                val name = songObject["name"].asString
                val artists = StringBuilder()
                songObject["ar"].getAsJsonArray().forEach(Consumer { artist: JsonElement ->
                    artists.append(
                        artist.getAsJsonObject()["name"].asString
                    )
                })
                val picUrl = songObject["al"].getAsJsonObject()["picUrl"].asString
                playList.add(Music(id1, name, artists.toString(), picUrl))
            }
        } catch (e: Exception) {
            // ignored
        }
        return playList
    }

    val songsFromDaily: PlayList
        get() {
            val playList = PlayList()
            try {
                val jsonObject: JsonObject
                val dailyList = dailyList
                jsonObject = gson.fromJson(dailyList, JsonObject::class.java)
                println(dailyList)
                val songs = jsonObject.getAsJsonObject("data").getAsJsonArray("dailySongs")
                for (song in songs) {
                    val songObject = song.getAsJsonObject()
                    val id1 = songObject["id"].asLong
                    val name = songObject["name"].asString
                    val artists = StringBuilder()
                    songObject["ar"].getAsJsonArray().forEach(Consumer { artist: JsonElement ->
                        artists.append(
                            artist.getAsJsonObject()["name"].asString
                        )
                    })
                    val picUrl = songObject["al"].getAsJsonObject()["picUrl"].asString
                    playList.add(Music(id1, name, artists.toString(), picUrl))
                }
            } catch (e: Exception) {
                // ignored
            }
            return playList
        }

    fun loadLyrics(music: Music) {
        try {
            val verbatimLyrics = getVerbatimLyrics(music.id)
            if (verbatimLyrics.isEmpty()) {
                return
            }
            val jsonObject = gson.fromJson(verbatimLyrics, JsonObject::class.java)
            //是否存在逐字歌词
            val yrc = jsonObject.getAsJsonObject("yrc")
            if (yrc != null) {
                val lyrics = yrc.getAsJsonPrimitive("lyric")
                if (lyrics != null) {
                    music.lyrics = parseLyrics(lyrics.asString)
                }
            } else {
                val lrc = jsonObject.getAsJsonObject("lrc")
                val lrcs = lrc.getAsJsonPrimitive("lyric")
                if (lrcs != null) {
                    music.lyrics = parseLyrics2(lrcs.asString)
                } else {
                    val lyrics = Lyrics()
                    val line = Line()
                    line.addWord(Word("暂无歌词", 0, Long.MAX_VALUE))
                    line.time = 0
                    line.duration = Long.MAX_VALUE
                    lyrics.addLine(line)
                    music.lyrics = lyrics
                }
            }
        } catch (e: Exception) {
            error("music", "an error occurred when loading lyrics of " + music.name)
            e.printStackTrace()
        }
    }

    private fun parseLyrics2(str: String): Lyrics {
        var str = str
        val lyrics = Lyrics()
        str = str.replace("\n".toRegex(), System.lineSeparator())
        val split1 = str.split(System.lineSeparator().toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (i in split1.indices) {
            if (split1[i].startsWith("[")) {
                val line = Line()
                val split = split1[i].split("]".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (split.size == 2) {
                    val time = split[0].substring(1)
                    line.timeTick = time
                    line.type = 1
                    val content = split[1]
                    line.addWord(Word(content, 0, 0))
                    lyrics.addLine(line)
                }
            }
        }
        return lyrics
    }

    private fun parseLyrics(str: String): Lyrics {
        var str = str
        val lyrics = Lyrics()
        str = str.replace("\n".toRegex(), System.lineSeparator()).replace("\"".toRegex(), "\"")
        for (s in str.split(System.lineSeparator().toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (s.startsWith("[")) {
                val line = Line()
                val split = s.split("]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val time = split[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].substring(1)
                val lineduration = split[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                line.time = time.toLong()
                line.duration = lineduration.toLong()
                val content = split[1]
                val words = content.split("\\(".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (word in words) {
                    if (word.isNotEmpty()) {
                        val split1 = word.split("\\)".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val split2 = split1[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val wordContent = split1[1]
                        val start = split2[0].toInt()
                        val duration = split2[1].toInt()
                        line.addWord(Word(wordContent, start.toLong(), duration.toLong()))
                    }
                }
                lyrics.addLine(line)
            }
        }
        return lyrics
    }

    fun searchSongs(keywords: String?): PlayList {
        return try {
            val playList = PlayList()
            val json = NeteaseApi.searchSongs(URLEncoder.encode(keywords, "UTF-8"))
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val result = jsonObject.getAsJsonObject("result")
            val songs = result.getAsJsonArray("songs")
            for (song in songs) {
                val songObject = song.getAsJsonObject()
                val id1 = songObject["id"].asLong
                val name = songObject["name"].asString
                val artists = StringBuilder()
                songObject["ar"].getAsJsonArray().forEach(Consumer { artist: JsonElement ->
                    artists.append(
                        artist.getAsJsonObject()["name"].asString
                    ).append(" ")
                })
                val picUrl = songObject["al"].getAsJsonObject()["picUrl"].asString
                playList.add(Music(id1, name, artists.toString(), picUrl))
            }
            playList
        } catch (e: Exception) {
            e.printStackTrace()
            PlayList()
        }
    }

    fun searchList(list: String?): PlayList {
        return getSongsFromList(list)
    }

    fun getLoginStatus(key: String?): JsonObject {
        val json = checkLoginStatus(key!!)
        return gson.fromJson(json, JsonObject::class.java)
    }
}
