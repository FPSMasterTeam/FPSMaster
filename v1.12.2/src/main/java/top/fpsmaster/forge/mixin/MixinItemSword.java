package top.fpsmaster.forge.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.fpsmaster.features.impl.optimizes.OldAnimations;

import static top.fpsmaster.features.impl.optimizes.OldAnimations.oldBlock;

@Mixin(Item.class)
public class MixinItemSword {
    /**
     * @author SuperSkidder
     * @reason blockAnimation
     */
    @Overwrite
    public EnumAction getItemUseAction(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword))
            return EnumAction.NONE;
        if (OldAnimations.using && oldBlock.getValue()) {
            return EnumAction.BLOCK;
        } else {
            return EnumAction.NONE;
        }
    }
    /**
     * @author SuperSkidder
     * @reason blockAnimation
     */
    @Overwrite
    public int getMaxItemUseDuration(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword))
            return 0;
        if (OldAnimations.using && oldBlock.getValue()) {
            return 72000;
        } else {
            return 0;
        }
    }
    /**
     * @author SuperSkidder
     * @reason blockAnimation
     */
    @Overwrite
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!(itemstack.getItem() instanceof ItemSword))
            return new ActionResult<>(EnumActionResult.PASS, itemstack);

        if (!oldBlock.getValue() || !OldAnimations.using) {
            return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.PASS, itemstack);
    }

}
