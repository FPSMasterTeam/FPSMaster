package top.fpsmaster.wrapper;

import net.minecraft.client.Minecraft;
import top.fpsmaster.forge.api.IMinecraft;
import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.interfaces.game.ITimerProvider;

public class TimerProvider implements ITimerProvider {
    public float getRenderPartialTicks(){
        return ((IMinecraft) Minecraft.getMinecraft()).arch$getTimer().renderPartialTicks;
    }
}
