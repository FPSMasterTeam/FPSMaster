package top.fpsmaster.wrapper.util;


import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class WrapperAxisAlignedBB {
    AxisAlignedBB axisAlignedBB;

    public WrapperAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    public WrapperAxisAlignedBB(double x, double y, double z, double x1, double y1, double z1) {
        this.axisAlignedBB = new AxisAlignedBB(x, y, z, x1, y1, z1);
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    public double minX() {
        return axisAlignedBB.minX;
    }

    public double minY() {
        return axisAlignedBB.minY;
    }

    public double minZ() {
        return axisAlignedBB.minZ;
    }

    public double maxX() {
        return axisAlignedBB.maxX;
    }

    public double maxY() {
        return axisAlignedBB.maxY;
    }

    public double maxZ() {
        return axisAlignedBB.maxZ;
    }

    public double getMinX() {
        return axisAlignedBB.minX;
    }

    public double getMinY() {
        return axisAlignedBB.minY;
    }

    public double getMinZ() {
        return axisAlignedBB.minZ;
    }

    public double getMaxX() {
        return axisAlignedBB.maxX;
    }

    public double getMaxY() {
        return axisAlignedBB.maxY;
    }

    public double getMaxZ() {
        return axisAlignedBB.maxZ;
    }

    public WrapperAxisAlignedBB expand(double x){
        axisAlignedBB = axisAlignedBB.expand(x, x, x);
        return this;
    }

    public WrapperAxisAlignedBB expand(double x, double y, double z){
        axisAlignedBB = axisAlignedBB.expand(x, y, z);
        return this;
    }

    public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB){
        return axisAlignedBB.calculateIntercept(vecA, vecB);
    }

    @NotNull
    public WrapperAxisAlignedBB addCoord(double x, double y, double z) {
        axisAlignedBB = axisAlignedBB.expand(x, y, z);
        return this;
    }

    public boolean isVecInside(@NotNull Vec3d vec3d) {
        return axisAlignedBB.contains(vec3d);
    }
}
