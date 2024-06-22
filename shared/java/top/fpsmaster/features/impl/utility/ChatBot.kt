package top.fpsmaster.features.impl.utility

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import top.fpsmaster.FPSMaster
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventPacket
import top.fpsmaster.event.events.EventSendChatMessage
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.ModeSetting
import top.fpsmaster.features.settings.impl.NumberSetting
import top.fpsmaster.features.settings.impl.TextSetting
import top.fpsmaster.ui.notification.addNotification
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.utils.thirdparty.openai.OpenAi
import top.fpsmaster.utils.thirdparty.openai.requestClientAI
import top.fpsmaster.interfaces.ProviderManager
import java.util.regex.Pattern
import kotlin.math.min


class ChatBot : Module("ChatBot", Category.Utility) {
    private var mode = ModeSetting("Mode", 0, "Internal", "Custom")
    private var apiKey = TextSetting("apiKey", "") { mode.isMode("Custom") }
    private var apiUrl = TextSetting("apiUrl", "https://api.openai.com/v1") { mode.isMode("Custom") }
    private var prompt = TextSetting(
        "prompt",
        "You are a chat bot in a Minecraft server"
    )
    private var maxContext = NumberSetting("maxContext", 6, 0, 10, 1)
    private var cooldown = NumberSetting("cooldown", 500, 0, 10000, 100)
    private var lastMsg: String = ""
    private var ignoreSelf = BooleanSetting("ignoreself", true)
    private var model = TextSetting("model", "gpt3.5-turbo")
    private var regex = TextSetting("regex", "<[^>]+> .*|[^>]+: .*")
    private var delay = NumberSetting("responddelay", 500, 0, 5000, 10)
    private var timer = MathTimer()

    private val filterPrompt: String =
        "你扮演一个Minecraft的聊天机器人，这是一条聊天栏消息，如果你觉得它来自玩家，就返回true，如果是系统消息或者其他的，就返回false"
    private var msgs: JsonArray = JsonArray()

    init {
        addSettings(mode, apiUrl, apiKey, model, maxContext, ignoreSelf, regex, prompt, delay, cooldown)
    }

    @Subscribe
    fun onSend(e: EventSendChatMessage) {
        if (ignoreSelf.value) {
            val s = e.msg
            lastMsg = s.substring(0, min(s.length, 20))
        }
    }

    @Subscribe
    fun onChat(e: EventPacket) {
        if (ProviderManager.packetChat.isPacket(e.packet) && timer.delay(cooldown.value.toLong())) {
            val formattedText = ProviderManager.packetChat.getUnformattedText(e.packet)
            if (formattedText.contains(lastMsg) && lastMsg.length > 1) {
                println(lastMsg)
                return
            }
            FPSMaster.async.runnable {
                val pattern = Pattern.compile(regex.value)
                val matcher = pattern.matcher(formattedText)
                if (matcher.find()) {
                    val openAi: OpenAi
                    addNotification("ChatGPT", formattedText, 1f)
                    val s: String
                    val userRole = JsonObject()
                    userRole.addProperty("role", "user")
                    userRole.addProperty("content", formattedText)
                    msgs.add(userRole)
                    if (mode.isMode("Custom")) {
                        openAi = OpenAi(apiUrl.value, apiKey.value, model.value, prompt.value)
                        s = openAi.requestNewAnswer(formattedText, msgs).replace("\n", "").trim()
                    } else {
                        val requestClientAI = requestClientAI(prompt.value, model.value, msgs)
                        if (requestClientAI[0] == "200") {
                            s = requestClientAI[1]!!
                        } else {
                            Utility.sendClientMessage("ChatGPT failed: " + requestClientAI[0])
                            return@runnable
                        }
                    }
                    lastMsg = s.substring(0, min(s.length, 20))
                    val aiRole = JsonObject()
                    aiRole.addProperty("role", "assistant")
                    aiRole.addProperty("content", s)
                    msgs.add(aiRole)
                    if (msgs.size() > maxContext.value.toInt()) {
                        msgs.drop(msgs.size() - maxContext.value.toInt())
                    }
                    Thread.sleep(delay.value.toLong())
                    if (ProviderManager.mcProvider.getPlayer() == null)
                        return@runnable
                    ProviderManager.mcProvider.getPlayer()!!.sendChatMessage(s)
                }
            }
        }
    }
}
