package top.fpsmaster.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.interfaces.ProviderManager;

import java.lang.reflect.Field;

public class Utility {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void sendClientMessage(String msg) {
        if (ProviderManager.mcProvider.getWorld() != null) {
            ProviderManager.mcProvider.printChatMessage(ProviderManager.utilityProvider.makeChatComponent(msg));
        }
    }

    public static boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static Field ofFastRender;

    public static boolean ofFastRender() {
        if (!FPSMaster.INSTANCE.getHasOptifine())
            return false;
        try {
            if (ofFastRender == null) {
                Class.forName("Config");
                ofFastRender = GameSettings.class.getDeclaredField("ofFastRender");
                ofFastRender.setAccessible(true);
            }
            return ofFastRender.getBoolean(mc.gameSettings);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException ignore) {
        }
        return false;
    }
}
