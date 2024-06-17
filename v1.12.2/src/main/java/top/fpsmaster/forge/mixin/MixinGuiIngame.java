package top.fpsmaster.forge.mixin;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.event.events.EventMotionBlur;
import top.fpsmaster.event.events.EventRender2D;
import top.fpsmaster.features.impl.interfaces.Scoreboard;
import top.fpsmaster.features.impl.render.Crosshair;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderHotbar", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        EventDispatcher.dispatchEvent(new EventRender2D(partialTicks));
    }

    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    protected void renderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci) {
        if (Crosshair.using)
            ci.cancel();
    }


    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void scoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (Scoreboard.using)
            ci.cancel();
    }
}
