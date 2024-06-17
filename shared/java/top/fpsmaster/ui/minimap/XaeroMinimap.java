package top.fpsmaster.ui.minimap;

import net.minecraft.client.Minecraft;
import top.fpsmaster.ui.minimap.interfaces.InterfaceHandler;

import java.io.IOException;

public class XaeroMinimap
{
    public static XaeroMinimap instance;
    public static Minecraft mc = Minecraft.getMinecraft();
    
    public void load() throws IOException {
        InterfaceHandler.loadPresets();
        InterfaceHandler.load();
    }
}
