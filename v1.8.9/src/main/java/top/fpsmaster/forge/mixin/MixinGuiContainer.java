package top.fpsmaster.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.utils.render.Render2DUtils;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Shadow
    protected int xSize = 176;
    @Shadow
    protected int ySize = 166;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerBackgroundLayer(FII)V"))
    public void logo(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        Render2DUtils.drawImage(new ResourceLocation("client/gui/settings/logo.png"), 0, sr.getScaledHeight() - 32, 163 / 2f, 32, -1);
    }

}
