package top.fpsmaster.forge.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.modules.logger.Logger;
import top.fpsmaster.modules.plugin.api.client.PluginGuiButton;
import top.fpsmaster.modules.plugin.api.wrapper.Util;

import java.util.List;
import java.util.Map;

@Mixin(GuiMultiplayer.class)
public abstract class MixinMultiPlayer extends MixinGuiScreen {


    @Inject(method = "createButtons", at = @At("RETURN"))
    public void onButton(CallbackInfo ci) {
        for (Map.Entry<String, PluginGuiButton> plugin : Util.buttons.entrySet()) {
            PluginGuiButton pluginGuiButton = plugin.getValue();
            this.buttonList.add(new GuiButton(pluginGuiButton.id, pluginGuiButton.x, pluginGuiButton.y, pluginGuiButton.width, pluginGuiButton.height, pluginGuiButton.text));
        }
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void action(GuiButton button, CallbackInfo ci) {
        Util.buttons.forEach((s, pluginGuiButton) -> {
            if (button.id == pluginGuiButton.id) {
                try {
                    pluginGuiButton.runnable.run();
                } catch (Exception e) {
                    Logger.error("Plugin Button", e.getMessage());
                }
            }
        });
    }
}
