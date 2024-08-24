package top.fpsmaster.interfaces

import top.fpsmaster.interfaces.client.IConstantsProvider
import top.fpsmaster.interfaces.game.*
import top.fpsmaster.interfaces.gui.IGuiIngameProvider
import top.fpsmaster.interfaces.gui.IGuiMainMenuProvider
import top.fpsmaster.interfaces.packets.IPacketChat
import top.fpsmaster.interfaces.packets.IPacketPlayerList
import top.fpsmaster.interfaces.packets.IPacketTimeUpdate
import top.fpsmaster.interfaces.render.IEffectRendererProvider
import top.fpsmaster.interfaces.render.IRenderManagerProvider
import top.fpsmaster.interfaces.sound.ISoundProvider
import top.fpsmaster.wrapper.*
import top.fpsmaster.wrapper.packets.SPacketChatProvider
import top.fpsmaster.wrapper.packets.SPacketPlayerListProvider
import top.fpsmaster.wrapper.packets.SPacketTimeUpdateProvider
import top.fpsmaster.wrapper.sound.SoundProvider

object ProviderManager {
    @JvmField
    val constants: IConstantsProvider = Constants()
    @JvmField
    var utilityProvider: IUtilityProvider = UtilityProvider()
    @JvmField
    val mcProvider: IMinecraftProvider = MinecraftProvider()
    @JvmField
    val mainmenuProvider: IGuiMainMenuProvider = GuiMainMenuProvider()
    @JvmField
    val skinProvider: ISkinProvider = SkinProvider()
    @JvmField
    val worldClientProvider: IWorldClientProvider = WorldClientProvider()
    @JvmField
    val timerProvider: ITimerProvider = TimerProvider()
    @JvmField
    val renderManagerProvider: IRenderManagerProvider = RenderManagerProvider()
    @JvmField
    val gameSettings: IGameSettings = GameSettingsProvider()

    // Packets
    @JvmField
    val packetChat: IPacketChat = SPacketChatProvider()
    @JvmField
    val packetPlayerList: IPacketPlayerList = SPacketPlayerListProvider()
    @JvmField
    val packetTimeUpdate: IPacketTimeUpdate = SPacketTimeUpdateProvider()
    @JvmField
    val guiIngameProvider: IGuiIngameProvider = GuiIngameProvider()
    @JvmField
    val soundProvider: ISoundProvider = SoundProvider()
    @JvmField
    val effectManager: IEffectRendererProvider = EffectRendererProvider()
}
