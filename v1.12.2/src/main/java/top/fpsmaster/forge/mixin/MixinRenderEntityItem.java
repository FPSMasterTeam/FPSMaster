package top.fpsmaster.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import top.fpsmaster.features.impl.render.ItemPhysics;

import java.util.Random;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem extends MixinRender{
    @Final
    @Shadow
    private final Random random = new Random();

    @Shadow
    protected abstract ResourceLocation getEntityTexture(EntityItem entity);

    @Final
    @Shadow
    private RenderItem itemRenderer;

    @Shadow
    protected abstract int transformModelCount(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_);
    @Shadow
    public abstract boolean shouldSpreadItems();

    @Shadow
    protected abstract int getModelCount(ItemStack stack);

    /**
     * @author SuperSkidder
     * @reason ItemPhysics
     */
    @Overwrite
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack itemstack = entity.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        this.random.setSeed(i);
        boolean flag = false;
        if (this.bindEntityTexture(entity)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, null);
        int j;
        if (ItemPhysics.using) {
            j = getModelCount(itemstack);
        } else {
            j = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
        }

        float f = 0.4f;
        float f1 = 0.4f;
        float f2 = 0.4f;
        boolean flag1 = ibakedmodel.isGui3d();

        if (ItemPhysics.using) {
            GlStateManager.translate((float) x, (float) y, (float) z);
            GlStateManager.scale(f, f1, f2);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw, 0.0F, 0.0F, 1.0F);
            if (flag1) {
                GlStateManager.translate(0.0D, 0.0D, -0.08D);
            } else {
                GlStateManager.translate(0.0D, 0.0D, -0.04D);
            }
            if (flag1 || Minecraft.getMinecraft().getRenderManager().options != null) {
                double rotation;
                if (flag1) {
                    if (!entity.onGround) {
                        rotation = 1.1D;
                        entity.rotationPitch = (float) ((double) entity.rotationPitch + rotation);
                    }
                } else if (!Double.isNaN(entity.posX) && !Double.isNaN(entity.posY) && !Double.isNaN(entity.posZ) && entity.world != null) {
                    if (entity.onGround) {
                        entity.rotationPitch = 0.0F;
                    } else {
                        rotation = 1.1D;
                        entity.rotationPitch = (float) ((double) entity.rotationPitch + rotation);
                    }
                }
                GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            for (int k = 0; k < j; ++k) {
                GlStateManager.pushMatrix();
                if (flag1) {
                    if (k > 0) {
                        float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f7, f9, f6);
                    }

                    Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(0.0F, 0.0F, 0.05375F);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            return;
        }


        float f7;
        float f9;
        if (!flag1) {
            float f3 = -0.0F * (float)(j - 1) * 0.5F;
            f7 = -0.0F * (float)(j - 1) * 0.5F;
            f9 = -0.09375F * (float)(j - 1) * 0.5F;
            GlStateManager.translate(f3, f7, f9);
        }

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        for(int k = 0; k < j; ++k) {
            IBakedModel transformedModel;
            if (flag1) {
                GlStateManager.pushMatrix();
                if (k > 0) {
                    f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(this.shouldSpreadItems() ? f7 : 0.0F, this.shouldSpreadItems() ? f9 : 0.0F, f6);
                }

                transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.pushMatrix();
                if (k > 0) {
                    f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    GlStateManager.translate(f7, f9, 0.0F);
                }

                transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.09375F);
            }
        }

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);
        if (flag) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

}
