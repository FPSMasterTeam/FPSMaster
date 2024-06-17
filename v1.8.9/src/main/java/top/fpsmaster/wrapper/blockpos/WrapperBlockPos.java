package top.fpsmaster.wrapper.blockpos;


import net.minecraft.util.BlockPos;

public class WrapperBlockPos {
    BlockPos pos;
    public WrapperBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public double getX() {
        return pos.getX();
    }

    public double getY() {
        return pos.getY();
    }

    public double getZ() {
        return pos.getZ();
    }

}
