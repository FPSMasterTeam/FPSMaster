package top.fpsmaster.modules.plugin.api.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Session;
import org.lwjgl.input.Mouse;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.features.manager.Module;
import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.modules.plugin.api.client.PluginScreen;
import top.fpsmaster.modules.plugin.api.client.PluginGuiButton;
import top.fpsmaster.ui.screens.mainmenu.MainMenu;
import top.fpsmaster.utils.PluginGui;
import top.fpsmaster.utils.Utility;
import top.fpsmaster.utils.render.Render2DUtils;

import java.util.HashMap;

public class Util {
    public static HashMap<String, PluginGuiButton> buttons = new HashMap<>();

    public static float getScreenWidth() {
        ScaledResolution sr = new ScaledResolution(Utility.mc);
        return sr.getScaledWidth();
    }

    public static float getScreenHeight() {
        ScaledResolution sr = new ScaledResolution(Utility.mc);
        return sr.getScaledHeight();
    }

    public static void displayMainMenu() {
        Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
    }

    public static void displayConnecting(PluginScreen parent, String name, String ip) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new PluginGui(parent), Minecraft.getMinecraft(), new ServerData(name, ip, false)));
    }

    public static String getI18n(String text) {
        return FPSMaster.i18n.get(text);
    }

    public static void drawOptimizedRect(float x, float y, float width, float height, int color) {
        Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, color);
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        Render2DUtils.drawRect(x, y, width, height, color);
    }

    public static void displayScreen(PluginScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(new PluginGui(screen));
    }


    public static void setSession(String username, String playerID, String token, String type) {
        ProviderManager.mcProvider.setSession(new Session(username, playerID, token, type));
    }

    public static String getClientUserName() {
        assert FPSMaster.accountManager != null;
        return FPSMaster.accountManager.getUsername();
    }

    public static boolean isMouseDown(int b) {
        return Mouse.isButtonDown(b);
    }

    public static void openModule(String mod, boolean state) {
        for (Module module : FPSMaster.moduleManager.getModules()) {
            if (module.getName().equals(mod)) {
                module.set(state);
            }
        }
    }

    public static String getClientUserToken() {
        assert FPSMaster.accountManager != null;
        return FPSMaster.accountManager.getToken();
    }
}
