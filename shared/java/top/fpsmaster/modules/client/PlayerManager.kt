package top.fpsmaster.modules.client

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import top.fpsmaster.FPSMaster
import top.fpsmaster.modules.account.AccountManager
import top.fpsmaster.event.EventDispatcher
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventCapeLoading
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.modules.logger.Logger
import top.fpsmaster.ui.screens.oobe.GuiLogin
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.modules.ornaments.CapeUtils
import top.fpsmaster.utils.os.HttpRequest
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.awt.GifUtil
import top.fpsmaster.wrapper.TextFormattingProvider
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PlayerManager {
    private var gameid = ""
    private var uuid = ""
    private var skin = ""
    private var cape = ""
    private var username = ""
    private var token = ""
    private var timer = MathTimer()

    init {
        EventDispatcher.registerListener(this)
    }

    @Subscribe
    fun onTick(e: EventTick) {
        if (ProviderManager.mcProvider.getPlayer() != null) {
            if (timer.delay(60000)) {
                updateData()
                allUsers
                for (playerEntity in ProviderManager.mcProvider.getWorld()!!.playerEntities) {
                    playerEntity?.let {
                        addPlayer(it.name, it.uniqueID.toString())
                    }
                }
            }
        }
    }

    private var gifNumber = 0
    private var gifData: MutableList<GifUtil.FrameData>? = null
    private var gifCount = 0
    private var gif = false
    private val gifTimer = MathTimer()

    @Subscribe
    fun onCape(e: EventCapeLoading) {
        try {
            val player = getPlayer(e.playerName)
            if (player.uuid != e.player.uniqueID.toString()) return

            if (player.skin.isNullOrEmpty().not() && player.skin != player.lastSkin) {
                player.lastSkin = player.skin
                FPSMaster.async.runnable {
                    ProviderManager.skinProvider.updateSkin(e.playerName, player.uuid, player.skin!!)
                }
            }

            if (player.cape.isEmpty()) return

            if (player.cape != player.lastCape) {
                FPSMaster.async.runnable {
                    player.lastCape = player.cape
                    val site = HttpRequest["${FPSMaster.SERVICE_API}/getItemResource?id=${player.cape}&timestamp=${System.currentTimeMillis()}"]
                    val parse = JsonParser().parse(site).asJsonObject
                    if (parse["code"].asInt == 200) {
                        val cape = parse["msg"].asString.trim()
                        if (cape.endsWith(".gif")) {
                            gifData = CapeUtils.downloadCapeGif(player.cape, cape)
                            gifCount = gifData!!.size
                            gifNumber = 0
                            gif = true
                        } else {
                            CapeUtils.downloadCape(player.cape, cape)
                            gif = false
                        }
                    }
                }
            }

            if (gif) {
                e.setCachedCape("fpsmaster/capes/${player.cape}_$gifNumber")
                if (gifTimer.delay(gifData?.get(gifNumber)?.delay?.toLong() ?: 0L)) {
                    gifNumber = (gifNumber + 1) % gifCount
                }
            } else {
                e.setCachedCape("fpsmaster/capes/${player.cape}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val allUsers: Unit
        get() {
            FPSMaster.async.runnable {
                val s = HttpRequest["${FPSMaster.SERVICE_API}/getUsers?timestamp=${System.currentTimeMillis()}"]
                val json = JsonParser().parse(s).asJsonObject
                if (json["code"].asInt == 200) {
                    playerList.clear()
                    json["data"].asJsonArray.forEach { e ->
                        playerList.add(e.asString)
                    }
                }
                if (FPSMaster.debug) {
                    Utility.sendClientMessage("获取所有用户信息 ${playerList.size} 个")
                }
            }
        }

    private fun updateData() {
        val player = ProviderManager.mcProvider.getPlayer()
        if (gameid == Minecraft.getMinecraft().session.playerID
            && uuid == player!!.uniqueID.toString()
            && skin == AccountManager.skin
            && cape == AccountManager.cape
        ) {
            return
        }

        gameid = try {
            URLEncoder.encode(player!!.name, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        uuid = player.uniqueID.toString()
        skin = AccountManager.skin
        cape = AccountManager.cape
        username = FPSMaster.accountManager.username
        token = FPSMaster.accountManager.token

        if (FPSMaster.debug) {
            Utility.sendClientMessage("更新用户信息: $gameid $uuid $skin $cape")
        }

        updateInformation(gameid, username, token, uuid, skin, cape)
    }

    private fun updateInformation(
        gameId: String,
        username: String,
        token: String,
        uuid: String,
        skin: String,
        cape: String
    ) {
        FPSMaster.async.runnable {
            if (ProviderManager.mcProvider.getPlayer() != null && token.isNotEmpty()) {
                val serverAddress = ProviderManager.mcProvider.getServerAddress()
                val url = "${FPSMaster.SERVICE_API}/update?api=v2&name=$gameId&username=$username&token=${token.trim()}&uuid=$uuid&skin=$skin&cape=$cape&address=$serverAddress&timestamp=${System.currentTimeMillis()}"
                val rets = HttpRequest[url]
                if (FPSMaster.INSTANCE.wsClient?.isOpen == true) {
                    FPSMaster.INSTANCE.wsClient!!.sendInformation(skin, cape, gameId, serverAddress)
                }
                if (JsonParser().parse(rets).asJsonObject["code"].asInt != 200) {
                    Logger.info("更新用户信息失败: $rets")
                }
            }
        }
    }

    private fun addPlayer(name: String, uuid: String) {
        if (name.isEmpty() || playerList.contains(name).not()) return

        Thread {
            val player = Player("", uuid)
            try {
                val u = "${FPSMaster.SERVICE_API}/getUser?name=${URLEncoder.encode(name, "UTF-8")}&uuid=$uuid&timestamp=${System.currentTimeMillis()}"
                val json = HttpRequest[u]
                if (json.isEmpty()) return@Thread
                val obj = JsonParser().parse(json).asJsonObject
                if (obj["code"].asInt == 200) {
                    val data = obj["data"].asJsonObject
                    player.username = data["username"].asString
                    player.skin = data["skin"].asString
                    player.rank = data["rank"].asString
                    ProviderManager.skinProvider.updateSkin(name, uuid, player.skin)
                    player.cape = data["cape"].asString
                    if (FPSMaster.debug) {
                        Utility.sendClientMessage("获取用户信息: $name $uuid ${player.rank}")
                    }
                }
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }

            clientMates[name] = player
        }.start()
    }

    fun getPlayer(name: String): Player = clientMates.getOrDefault(name, Player("", ""))

    fun getPlayerRank(name: String): String = clientMates[name]?.rank.orEmpty()

    inner class Player(rank: String, val uuid: String) {
        var username = ""
        var hytRank = rank
        var rank = rank
        var lastSkin: String? = ""
        var skin: String? = ""
        var cape = ""
        var lastCape = ""
    }

    companion object {
        val playerList: MutableList<String> = CopyOnWriteArrayList()
        val clientMates: ConcurrentHashMap<String, Player> = ConcurrentHashMap()
    }
}
