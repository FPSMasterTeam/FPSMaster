package top.fpsmaster.wrapper.util;


import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrapperVec3 {
    Vec3 vec3d;

    public WrapperVec3(Vec3 vec3d) {
        this.vec3d = vec3d;
    }

    public double x(){
        return vec3d.xCoord;
    }

    public double y(){
        return vec3d.yCoord;
    }

    public double z(){
        return vec3d.zCoord;
    }

    public double distanceTo(Object vec3d3) {
        return this.vec3d.distanceTo((Vec3) vec3d3);
    }

    @NotNull
    public Vec3 addVector(double x, double y, double z) {
        return this.vec3d.addVector(x, y, z);
    }
}
