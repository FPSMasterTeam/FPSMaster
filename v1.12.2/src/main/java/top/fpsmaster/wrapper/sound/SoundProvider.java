package top.fpsmaster.wrapper.sound;

import net.minecraft.init.SoundEvents;
import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.interfaces.ProviderManager;

public class SoundProvider {
    public static void playLightning(double posX, double posY, double posZ, int i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, net.minecraft.util.SoundCategory.BLOCKS, i, v, b);
    }
    public static void playExplosion(double posX, double posY, double posZ, int i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, net.minecraft.util.SoundCategory.BLOCKS, i, v, b);
    }
}
