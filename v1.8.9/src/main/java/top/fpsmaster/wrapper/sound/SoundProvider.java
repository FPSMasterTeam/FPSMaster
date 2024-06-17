package top.fpsmaster.wrapper.sound;

import top.fpsmaster.interfaces.ProviderManager;

public class SoundProvider {
    public static void playLightning(double posX, double posY, double posZ, int i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, "lightning", i, v, b);
    }

    public static void playExplosion(double posX, double posY, double posZ, int i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, "explosion", i, v, b);
    }
}
