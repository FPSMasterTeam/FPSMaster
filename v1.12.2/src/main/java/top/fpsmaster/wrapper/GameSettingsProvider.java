package top.fpsmaster.wrapper;

import net.minecraft.client.settings.KeyBinding;
import top.fpsmaster.forge.api.IKeyBinding;
import top.fpsmaster.interfaces.game.IGameSettings;

public class GameSettingsProvider implements IGameSettings {
    public void setKeyPress(KeyBinding key, boolean value){
        ((IKeyBinding) key).setPressed(value);
    }
}
