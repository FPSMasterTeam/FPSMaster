package top.fpsmaster.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.event.events.EventCapeLoading;
import top.fpsmaster.features.impl.optimizes.SmoothZoom;
import top.fpsmaster.features.impl.utility.CustomFOV;
import top.fpsmaster.utils.math.MathUtils;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    private ResourceLocation fpsmasterCape;

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    public void customFov(CallbackInfoReturnable<Float> cir) {
        float f = 1.0F;
        if (CustomFOV.using) {
            if ((!CustomFOV.noFlyFov.getValue())) {
                if (capabilities.isFlying) {
                    f *= 1.1F;
                }
            }

            if ((!CustomFOV.noSpeedFov.getValue())) {
                IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                f = (float) ((double) f * ((iattributeinstance.getAttributeValue() / (double) this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));
            }

            if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
                f = 1.0F;
            }

            if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
                int i = this.getItemInUseDuration();
                float f1 = (float) i / 20.0F;
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }

                f *= 1.0F - f1 * 0.15F;
            }
            cir.setReturnValue(f);
        }
    }

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Shadow
    private NetworkPlayerInfo playerInfo;


    /**
     * @author SuperSkidder
     * @reason CapeLoading
     */
    @Overwrite
    public ResourceLocation getLocationCape() {
        EventCapeLoading event = new EventCapeLoading(playerInfo.getGameProfile().getName(), (AbstractClientPlayer) (Object) this);
        EventDispatcher.dispatchEvent(event);
        fpsmasterCape = event.getCape();

        if (fpsmasterCape != null) {
            return fpsmasterCape;
        }


        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();

    }
}
