package top.fpsmaster.features.impl.utility

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import top.fpsmaster.FPSMaster
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventPacket
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.ui.notification.addNotification
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.utils.os.HttpRequest
import top.fpsmaster.wrapper.packets.SPacketChatProvider
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap


class CheatersDetector : Module("CheatersDetector", Category.Utility) {

    init {
        addSettings(autoHub, autoReport, cloud)
    }

    override fun onEnable() {
        val serverAddress = ProviderManager.mcProvider.getServerAddress()
        if (serverAddress?.contains("hytpc") == false) {
            Utility.sendClientMessage("您疑似在非花雨庭服务器内开启了作弊者侦测功能，若不关闭可能引起问题")
        }
        super.onEnable()
    }
    val timer = MathTimer()
    @Subscribe
    fun onTick(e: EventTick) {
        if (ProviderManager.mcProvider.getPlayer() == null)
            return
        if (timer.delay(30000)) {
            for (entity in ProviderManager.mcProvider.getPlayerInfoMap()!!) {
                if (autoHub.value && hackers.keys.toList().contains(entity!!.gameProfile.id.toString())) {
                    ProviderManager.mcProvider.getPlayer()!!.sendChatMessage("/hub")
                }
            }

            // update hackers list
            if (cloud.value) {
                FPSMaster.async.runnable {
                    try {
                        val s =
                            HttpRequest[
                                FPSMaster.SERVICE_API +
                                        "/blacklist/get?timestamp=${System.currentTimeMillis()}",
                            ]
                        val jsonObject: JsonObject = JsonParser().parse(s).asJsonObject.getAsJsonObject("data")

                        for ((key1, value) in jsonObject.entrySet()) {
                            hackers[key1] = value.asString
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @Subscribe
    fun onChat(e: EventPacket) {
        if (ProviderManager.packetChat.isPacket(e.packet)) {
            val input = ProviderManager.packetChat.getUnformattedText(e.packet)
            val pattern = "<([^>]*)>".toRegex()
            var matchResult = pattern.find(input)
            if (matchResult == null){
                val pattern2 = ">([^>]*):".toRegex()
                matchResult = pattern2.find(input) ?: return
            }

            val result = matchResult.groups[1]?.value
            for (entity in ProviderManager.mcProvider.getPlayerInfoMap()!!) {
                if (entity!!.gameProfile.name != result)
                    continue
                // 开始对消息进行关键词过滤
                val keywords =
                    arrayOf(
                        "xinxin.fan",
                        "公益",
                        "快速宏",
                        "QuickMarco",
                        "SilenceFix",
                        "styles.wtf",
                        "southside",
                        "老安卓",
                        "MLFK",
                        "xylitol",
                    )
                for (keyword in keywords) {
                    if (!input.contains(keyword))
                        continue
                    if (hackers.keys().toList().contains(entity.gameProfile.id.toString()))
                        return
                    addNotification("检测到外挂", "检测到玩家${entity.gameProfile.name}发送了可疑消息: $keyword", 3f)
                    if (autoReport.value) {
                        addNotification("检测到外挂", "已自动举报玩家${entity.gameProfile.name}", 3f)
                        ProviderManager.mcProvider.getPlayer()!!.sendChatMessage("/report ${entity.gameProfile.name}")
                    }
                    FPSMaster.async.runnable {
                        HttpRequest[
                            FPSMaster.SERVICE_API +
                                    "/blacklist/commit?username=${FPSMaster.INSTANCE.accountManager!!.username}&token=${FPSMaster.INSTANCE.accountManager!!.token}&player=${
                                        URLEncoder.encode(
                                            entity.gameProfile.name,
                                            "UTF-8"
                                        )
                                    }&uuid=${entity.gameProfile.id}",
                        ]
                    }
                    hackers[entity.gameProfile.id.toString()] = entity.gameProfile.name
                }
            }
        }

    }

    companion object {
        private val hackers = ConcurrentHashMap<String, String>()
        val autoHub = BooleanSetting("AutoHub", true)
        val autoReport = BooleanSetting("AutoReport", false)
        val cloud = BooleanSetting("Cloud", true)

        fun filter(result: String): String {
            hackers.values.forEach {
                if (result.contains(it)) {
                    return result.replace(it, "§4§l[外挂]§r§7$it")
                }
            }
            return result
        }
    }

}