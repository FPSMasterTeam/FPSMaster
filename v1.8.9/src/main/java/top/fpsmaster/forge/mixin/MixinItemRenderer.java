package top.fpsmaster.forge.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.features.impl.optimizes.OldAnimations;
import top.fpsmaster.features.impl.render.FireModifier;

import java.awt.*;

import static top.fpsmaster.features.impl.optimizes.OldAnimations.*;
import static top.fpsmaster.utils.Utility.mc;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {


    @Shadow
    private ItemStack itemToRender;
    @Shadow
    private float equippedProgress;
    @Shadow
    private float prevEquippedProgress;

    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void renderFireInFirstPerson(CallbackInfo ci) {
        if (FireModifier.using) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            float f = 1.0F;

            for (int i = 0; i < 2; ++i) {
                GlStateManager.pushMatrix();
                TextureAtlasSprite textureatlassprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                float f1 = textureatlassprite.getMinU();
                float f2 = textureatlassprite.getMaxU();
                float f3 = textureatlassprite.getMinV();
                float f4 = textureatlassprite.getMaxV();
                float f5 = -0.5F;
                float f6 = 0.5F;
                float f7 = -0.5F;
                float f8 = 0.5F;
                float f9 = -0.5F;
                GlStateManager.translate(0, FireModifier.using ? -FireModifier.height.getValue().floatValue() : 0, 0);
                if (FireModifier.using && FireModifier.customColor.getValue()) {
                    Color color = FireModifier.colorSetting.getColor();
                    GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.9F);
                }
                GlStateManager.translate((float) (-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
                GlStateManager.rotate((float) (i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-0.5, -0.5, -0.5).tex(f2, f4).endVertex();
                bufferbuilder.pos(0.5, -0.5, -0.5).tex(f1, f4).endVertex();
                bufferbuilder.pos(0.5, 0.5, -0.5).tex(f1, f3).endVertex();
                bufferbuilder.pos(-0.5, 0.5, -0.5).tex(f2, f3).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            ci.cancel();
        }
    }


    /**
     * @author SuperSkidder
     * @reason animation
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer abstractclientplayer = mc.thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP)abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        if (this.itemToRender != null) {
            if (OldAnimations.oldRod.getValue() && itemToRender.getItem() instanceof ItemCarrotOnAStick) {
                GlStateManager.translate(0.08F, -0.027F, -0.33F);
                GlStateManager.scale(0.93F, 1.0F, 1.0F);
            }
            if (OldAnimations.oldRod.getValue() && itemToRender.getItem() instanceof ItemFishingRod) {
                GlStateManager.translate(0.08F, -0.027F, -0.33F);
                GlStateManager.scale(0.93F, 1.0F, 1.0F);
            }
            if (OldAnimations.oldSwing.getValue() && f1 != 0.0F && !mc.thePlayer.isBlocking() && !mc.thePlayer.isEating() && !mc.thePlayer.isUsingItem()) {
                GlStateManager.scale(0.85F, 0.85F, 0.85F);
                GlStateManager.translate(-0.06F, 0.003F, 0.05F);
            }
            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();
                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(mc.thePlayer, partialTicks);
                        this.transformFirstPersonItem(f, f1);
                        break;

                    case BLOCK:
                        if (OldAnimations.blockHit.getValue()) {
                            this.transformFirstPersonItem(f, f1);
                        } else {
                            this.transformFirstPersonItem(f, 0.0F);
                        }
                        this.doBlockTransformations();
                        break;

                    case BOW:
                        if (OldAnimations.oldBow.getValue()) {
                            this.transformFirstPersonItem(f, f1);
                        } else {
                            this.transformFirstPersonItem(f, 0.0F);
                        }
                        this.doBowTransformations(partialTicks, mc.thePlayer);
                }
            } else {
                this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }
}
