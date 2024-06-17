package top.fpsmaster.interfaces.game

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.item.ItemStack
import net.minecraft.util.Session
import top.fpsmaster.interfaces.IProvider
import java.io.File

interface IMinecraftProvider : IProvider {
    fun getCurrentScreen(): Any?
    fun getGameDir(): File
    fun getFontRenderer(): FontRenderer
    fun getPlayer(): EntityPlayerSP?
    fun isHoveringOverBlock(): Boolean
    fun getPlayerHeldItem(): ItemStack?
    fun getWorld(): WorldClient?
    fun getArmorInventory(): Array<ItemStack?>
    fun setSession(mojang: Session?)
    fun getRespondTime(): Int?
    fun drawString(text: String?, x: Float, y: Float, color: Int)
    fun getServerAddress(): String?
    fun removeClickDelay()
    fun printChatMessage(message: Any)
    fun getPlayerInfoMap(): Collection<NetworkPlayerInfo?>?
}