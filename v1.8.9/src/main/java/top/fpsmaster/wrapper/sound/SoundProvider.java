package top.fpsmaster.wrapper.sound;

import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.interfaces.sound.ISoundProvider;

public class SoundProvider implements ISoundProvider {
    public void playLightning(double posX, double posY, double posZ, float i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, "lightning", i, v, b);
    }

    public void playExplosion(double posX, double posY, double posZ, float i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, "explosion", i, v, b);
    }

    public void playRedStoneBreak(double posX, double posY, double posZ, float i, float v, boolean b) {
        ProviderManager.mcProvider.getWorld().playSound(posX, posY, posZ, "dig.stone", i, v, b);
    }
}
