package top.fpsmaster.wrapper;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

public class WrapperEntityLightningBolt extends EntityLightningBolt {
    public WrapperEntityLightningBolt(World worldIn, double x, double y, double z, boolean effectOnlyIn) {
        super(worldIn, x, y, z);
    }
}
