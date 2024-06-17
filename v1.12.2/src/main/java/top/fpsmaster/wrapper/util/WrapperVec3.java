package top.fpsmaster.wrapper.util;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrapperVec3 {
    Vec3d vec3d;

    public WrapperVec3(Vec3d vec3d) {
        this.vec3d = vec3d;
    }

    public double x(){
        return vec3d.x;
    }

    public double y(){
        return vec3d.y;
    }

    public double z(){
        return vec3d.z;
    }

    public double distanceTo(Object vec3d3) {
        return this.vec3d.distanceTo((Vec3d) vec3d3);
    }

    public Vec3d addVector(double x, double y, double z) {
        return this.vec3d.add(x, y, z);
    }
}
