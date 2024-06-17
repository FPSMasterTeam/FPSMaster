package top.fpsmaster.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.features.impl.optimizes.FixedInventory;

import java.util.Iterator;

import static top.fpsmaster.utils.Utility.mc;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer extends MixinGuiContainer{

    @Shadow
    private boolean hasActivePotionEffects;
    
    @Inject(method = "updateActivePotionEffects", at = @At("RETURN"))
    private void renderPotionEffects(CallbackInfo ci) {
        boolean hasVisibleEffect = false;
        Iterator var2 = mc.player.getActivePotionEffects().iterator();

        while(var2.hasNext()) {
            PotionEffect potioneffect = (PotionEffect)var2.next();
            Potion potion = potioneffect.getPotion();
            if (potion.shouldRender(potioneffect)) {
                hasVisibleEffect = true;
                break;
            }
        }

        if (!mc.player.getActivePotionEffects().isEmpty() && hasVisibleEffect) {
            if (FixedInventory.using){
                this.guiLeft = (mc.currentScreen.width - this.xSize) / 2;
            }else {
                this.guiLeft = 160 + (mc.currentScreen.width - this.xSize - 200) / 2;
            }
            this.hasActivePotionEffects = true;
        } else {
            this.guiLeft = (mc.currentScreen.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }
}
