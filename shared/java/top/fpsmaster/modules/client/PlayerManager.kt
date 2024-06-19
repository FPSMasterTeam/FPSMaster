package top.fpsmaster.modules.client

import com.google.gson.JsonElement
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
import top.fpsmaster.utils.ornaments.capes.CapeUtils
import top.fpsmaster.utils.os.HttpRequest
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.awt.GifUtil
import top.fpsmaster.wrapper.TextFormattingProvider
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class PlayerManager {
    private var gameid: String = ""
    private var uuid: String = ""
    private var skin: String = ""
    private var cape: String = ""
    private var username: String = ""
    private var token: String = ""

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
                    if (playerEntity == null)
                        continue
                    addPlayer(playerEntity.name, playerEntity.uniqueID.toString())
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
            val player = FPSMaster.INSTANCE.playerManager!!.getPlayer(e.playerName)
            if (player.uuid != e.player.uniqueID.toString()) return
            if (player.skin != null && player.skin != player.lastSkin && player.skin!!.isNotEmpty()) {
                player.lastSkin = player.skin
                FPSMaster.async.runnable {
                    ProviderManager.skinProvider.updateSkin(e.playerName, player.uuid, player.skin)
                }
            }
            if (player.cape.isEmpty()) {
                return
            }
            if (player.cape != player.lastCape) {
                FPSMaster.async.runnable {
                    player.lastCape = player.cape
                    val site =
                        HttpRequest["${FPSMaster.SERVICE_API}/getItemResource?id=${player.cape}&timestamp=${System.currentTimeMillis()}"]
                    val parse = JsonParser().parse(site).getAsJsonObject()
                    if (parse != null && parse["code"].asInt == 200) {
                        val cape = parse["msg"].asString.trim { it <= ' ' }
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
                if (gifTimer.delay(gifData?.get(gifNumber)!!.delay.toLong())) {
                    gifNumber++
                    if (gifNumber >= gifCount) gifNumber = 0
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
                val json = JsonParser().parse(s).getAsJsonObject()
                if (json["code"].asInt == 200) {
                    playerList.clear()
                    val data = json["data"].getAsJsonArray()
                    data.forEach(Consumer { e: JsonElement ->
                        val name = e.asString
                        playerList.add(name)
                    })
                }
                if (FPSMaster.debug)
                    Utility.sendClientMessage("获取所有用户信息 ${playerList.size} 个")
            }
        }

    private fun updateData() {
        if (gameid == Minecraft.getMinecraft().session.playerID
            && uuid == ProviderManager.mcProvider.getPlayer()!!.uniqueID.toString()
            && skin == AccountManager.skin
            && cape == AccountManager.cape
        ) {
            return
        }
        gameid = try {
            URLEncoder.encode(ProviderManager.mcProvider.getPlayer()!!.name, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        uuid = ProviderManager.mcProvider.getPlayer()!!.uniqueID.toString()
        skin = AccountManager.skin
        cape = AccountManager.cape
        username = FPSMaster.INSTANCE.accountManager!!.username
        token = FPSMaster.INSTANCE.accountManager!!.token
        if (FPSMaster.debug)
            Utility.sendClientMessage("更新用户信息: $gameid $uuid $skin $cape")
        updateInformation(
            gameid,
            username,
            token,
            uuid,
            skin,
            cape
        )
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
                val url = "${FPSMaster.SERVICE_API}/update?api=v2&name=" + gameId +
                        "&username=" + username +
                        "&token=" + token.trim { it <= ' ' } +
                        "&uuid=" + uuid +
                        "&skin=" + skin +
                        "&cape=" + cape +
                        "&address=" + serverAddress +
                        "&timestamp=${System.currentTimeMillis()}"
                val rets = HttpRequest[url]
                if (FPSMaster.INSTANCE.wsClient!!.isOpen) {
                    FPSMaster.INSTANCE.wsClient!!.sendInformation(skin, cape, gameId, serverAddress)
                }
                if (JsonParser().parse(rets).asJsonObject["code"].asInt != 200) {
                    Logger.info("更新用户信息失败: $rets")
                    Minecraft.getMinecraft().displayGuiScreen(GuiLogin())
                }
            }
        }
    }


    private fun addPlayer(name: String, uuid: String) {
        if (name.isEmpty()) return
        if (!playerList.contains(name)) return

        Thread(Runnable {
            val i: Int
            var r = ""
            try {
                val rank = HytApi().getRank(name)

                i = rank.toInt()
                if (i < 1000) {
                    r = TextFormattingProvider.getRed()
                        .toString() + " " + TextFormattingProvider.getBold() + "[" + i + "]" + TextFormattingProvider.getReset()
                } else if (i < 9999) {
                    r = TextFormattingProvider.getYellow()
                        .toString() + " " + TextFormattingProvider.getBold() + "[" + i / 1000 + "k]" + TextFormattingProvider.getReset()
                } else if (i > 10000) {
                    r = TextFormattingProvider.getYellow()
                        .toString() + " " + TextFormattingProvider.getBold() + "[" + i / 10000 + "w]" + TextFormattingProvider.getReset()
                }
            } catch (ignored: NumberFormatException) {
            }



            val json: String
            val player = Player(r, uuid)
            try {
                val u =
                    "${FPSMaster.SERVICE_API}/getUser?name=" + URLEncoder.encode(
                        name,
                        "UTF-8"
                    ) + "&uuid=" + uuid + "&timestamp=${System.currentTimeMillis()}"
                json = HttpRequest[u]
                if (json.isEmpty()) return@Runnable
                val obj = JsonParser().parse(json).getAsJsonObject()
                if (obj != null) {
                    if (obj["code"].asInt == 200) {
                        val data = obj["data"].getAsJsonObject()
                        player.username = data["username"].asString
                        player.skin = data["skin"].asString
                        player.rank = data["rank"].asString
                        ProviderManager.skinProvider.updateSkin(name, uuid, player.skin)
                        player.cape = data["cape"].asString
                        if (FPSMaster.debug)
                            Utility.sendClientMessage("获取用户信息: $name $uuid ${player.rank}")
                    }
                }
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }

            clientMates[name] = player
        }).start()
    }

    fun getPlayer(name: String): Player {
        return clientMates.getOrDefault(name, Player("", ""))
    }

    fun getPlayerRank(name: String): String {
        val player = clientMates[name]
        return player?.rank ?: ""
    }

    inner class Player(rank: String, uuid: String) {
        var username = ""

        @JvmField
        var hytRank = ""
        var rank = ""
        var uuid: String
        var lastSkin: String? = ""
        var skin: String? = ""

        @JvmField
        var cape: String = ""
        var lastCape: String = ""

        init {
            hytRank = rank
            this.uuid = uuid
        }
    }

    companion object {
        var playerList: MutableList<String?> = CopyOnWriteArrayList()
        var clientMates = ConcurrentHashMap<String, Player>()
    }
}
