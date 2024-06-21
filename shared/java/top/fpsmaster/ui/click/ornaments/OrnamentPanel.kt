package top.fpsmaster.ui.click.ornaments

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import top.fpsmaster.FPSMaster
import top.fpsmaster.modules.account.AccountManager.Companion.cape
import top.fpsmaster.utils.math.MathTimer
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.os.HttpRequest
import top.fpsmaster.utils.render.Render2DUtils
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.ui.click.component.ScrollContainer
import java.util.concurrent.CopyOnWriteArrayList

class OrnamentPanel {
    private val container = ScrollContainer()

    private val capes: CopyOnWriteArrayList<Item>
        get() {
            val msg = HttpRequest["${FPSMaster.SERVICE_API}/getItems?timestamp=${System.currentTimeMillis()}"]
            val newItems = CopyOnWriteArrayList<Item>()
            if (msg.isNotEmpty()) {
                val asJsonObject = JsonParser().parse(msg).getAsJsonObject()
                if (asJsonObject["code"].asInt == 200) {
                    for (element in asJsonObject["data"].getAsJsonArray()) {
                        val obj = element.getAsJsonObject()
                        if (obj["category"].asString != "capes") continue
                        newItems.add(
                            Item(
                                obj["category"].asString,
                                obj["itemId"].asString,
                                obj["name"].asString,
                                obj["price"].asString,
                                obj["img"].asString
                            )
                        )
                    }
                }
            }
            return newItems
        }
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    init {
        if (timer.delay(10000)) {
            Thread {
                val capes = capes
                items.clear()
                items.addAll(capes)
                FPSMaster.accountManager.getItems(
                    FPSMaster.accountManager.username,
                    FPSMaster.accountManager.token
                )
            }.start()
        }
    }

    fun drawScreen(x: Float, y: Float, width: Float, height: Float, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        var dX = 0f
        var dY = 20 + container.getRealScroll()
        var containerHeight = 0f
        container.draw(x, y + 10, width - 10f, height - 10f, mouseX, mouseY) {
            for (item in items) {
                if (!hasItem(item.itemId)) continue
                if (item.itemId == cape) {
                    Render2DUtils.drawOptimizedRoundedRect(
                        x + item.x,
                        y + item.y,
                        60f,
                        90f,
                        FPSMaster.theme.getPrimary()
                    )
                } else if (Render2DUtils.isHovered(x + item.x, y + item.y, 60f, 90f, mouseX, mouseY)) {
                    Render2DUtils.drawOptimizedRoundedRect(
                        x + item.x,
                        y + item.y,
                        60f,
                        90f,
                        Render2DUtils.reAlpha(FPSMaster.theme.getPrimary(), 100)
                    )
                } else {
                    Render2DUtils.drawOptimizedRoundedRect(
                        x + item.x,
                        y + item.y,
                        60f,
                        90f,
                        FPSMaster.theme.getFrontBackground()
                    )
                }
                if (item.imgLoaded) {
                    Render2DUtils.drawImage(
                        ResourceLocation("ornaments/" + item.name + "_preview"),
                        x + item.x + 3,
                        y + item.y + 3,
                        54f,
                        84f,
                        -1
                    )
                }
                item.x = base(item.x.toDouble(), dX.toDouble(), 0.2).toFloat()
                item.y = base(item.y.toDouble(), dY.toDouble(), 0.2).toFloat()
                dX += 90f
                if (dX + 60 > width) {
                    dX = 0f
                    dY += 120f
                    containerHeight += 120f

                }
            }
            container.setHeight(containerHeight + 120f)
        }
    }

    fun hasItem(id: String): Boolean {
        for (s in FPSMaster.accountManager.itemsHeld) {
            if (s == id) {
                return true
            }
        }
        return false
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        var dX = 0f
        var dY = 20 + container.getRealScroll()
        for (item in items) {
            if (!hasItem(item.itemId)) continue
            if (Render2DUtils.isHovered(x + item.x, y + item.y, 60f, 90f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                if (item.itemId == cape) {
                    cape = ""
                    FPSMaster.playerManager.getPlayer(ProviderManager.mcProvider.getPlayer()!!.name).cape =
                        ""
                } else {
                    cape = item.itemId
                    FPSMaster.playerManager.getPlayer(ProviderManager.mcProvider.getPlayer()!!.name).cape =
                        item.itemId
                }
            }
            dX += 90f
            if (dX + 60 > width) {
                dX = 0f
                dY += 120f
                if (dY > width) {
                    break
                }
            }
        }
    }

    companion object {
        var items: MutableList<Item> = CopyOnWriteArrayList()
        var timer = MathTimer()
    }
}
