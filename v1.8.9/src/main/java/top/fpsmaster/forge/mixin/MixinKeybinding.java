package top.fpsmaster.forge.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.forge.api.IKeyBinding;

@Mixin(KeyBinding.class)
@Implements(@Interface(iface = IKeyBinding.class, prefix = "fpsmaster$"))
public class MixinKeybinding implements IKeyBinding {

    @Shadow
    private boolean pressed;

    @Override
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
