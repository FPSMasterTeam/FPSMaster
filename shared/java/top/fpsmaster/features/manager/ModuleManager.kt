package top.fpsmaster.features.manager

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import top.fpsmaster.event.EventDispatcher.registerListener
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.*
import top.fpsmaster.modules.logger.Logger.warn
import top.fpsmaster.features.impl.interfaces.*
import top.fpsmaster.features.impl.optimizes.*
import top.fpsmaster.features.impl.render.*
import top.fpsmaster.features.impl.utility.*
import top.fpsmaster.interfaces.ProviderManager
import top.fpsmaster.ui.click.MainPanel
import top.fpsmaster.utils.Utility
import java.util.*

class ModuleManager {
    var modules = LinkedList<Module>()
    private val mainPanel: GuiScreen = MainPanel(true)
    fun getModule(mod: Class<*>): Module {
        for (module in modules) {
            if (module.javaClass == mod) {
                return module
            }
        }
        warn("Missing module:" + mod.name)
        return Module("missing module", "mission", Category.Utility)
    }

    @Subscribe
    fun onKey(e: EventKey) {
        if (e.key == ClickGui.keyBind.value) if (Minecraft.getMinecraft().currentScreen == null) Minecraft.getMinecraft()
            .displayGuiScreen(mainPanel)
        for (module in modules) {
            if (e.key == module.key) {
                module.toggle()
            }
        }

        // 提醒一下lowiq怎么组队
        if (ProviderManager.mcProvider.getPlayer() != null && ProviderManager.mcProvider.getServerAddress()?.contains("hytpc") == true) {
            if (e.key == Keyboard.KEY_K) {
                Utility.sendClientMessage("花雨庭组队提示：")
                Utility.sendClientMessage("创建队伍并邀请玩家：/组队 玩家名")
                Utility.sendClientMessage("接受组队邀请：/组队 邀请你的玩家名 【如果接受方是网易版请忽略该步骤】")
                Utility.sendClientMessage("解散队伍：/组队 解散")
            }
        }
    }

    fun init() {

        // register listener
        registerListener(this)
        // add mods
        modules.add(ClickGui())
        modules.add(BetterScreen())
        modules.add(Sprint())
        modules.add(Performance())
        modules.add(MotionBlur())
        modules.add(SmoothZoom())
        modules.add(FullBright())
        modules.add(ItemPhysics())
        modules.add(MinimizedBobbing())
        modules.add(MoreParticles())
        modules.add(FPSDisplay())
        modules.add(IRC())
        modules.add(ArmorDisplay())
        modules.add(BetterChat())
        modules.add(ComboDisplay())
        modules.add(CPSDisplay())
        modules.add(PotionDisplay())
        modules.add(ReachDisplay())
        modules.add(Scoreboard())
        modules.add(MusicOverlay())
        modules.add(OldAnimations())
        modules.add(HitColor())
        modules.add(BlockOverlay())
        modules.add(DragonWings())
        modules.add(FireModifier())
        modules.add(FreeLook())
        modules.add(LyricsDisplay())
        modules.add(SkinChanger())
        modules.add(TimeChanger())
        modules.add(TNTTimer())
        modules.add(Hitboxes())
        modules.add(LevelTag())
        modules.add(Keystrokes())
        modules.add(Crosshair())
        modules.add(CustomFOV())
//        modules.add(TabOverlay())
        modules.add(InventoryDisplay())
        modules.add(PlayerDisplay())
        modules.add(TargetDisplay())
        modules.add(NoHurtCam())
        modules.add(NameProtect())
//        modules.add(ChatBot())
//        modules.add(Translator())
        modules.add(RawInput())
        modules.add(FixedInventory())
        modules.add(NoHitDelay())
        modules.add(PingDisplay())
        modules.add(CoordsDisplay())
        modules.add(ModsList())
        modules.add(ClientCommand())
        modules.add(MiniMap())
        modules.add(DirectionDisplay())

        if (ProviderManager.constants.getVersion() == "1.12.2") {
            modules.add(HideIndicator())
        }
    }
}
