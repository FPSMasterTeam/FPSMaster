package top.fpsmaster.wrapper.renderEngine.bufferbuilder;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class WrapperBufferBuilder {
    private final WorldRenderer buffer;
    private final Tessellator tessellator;

    public WrapperBufferBuilder(Tessellator tessellator) {
        this.tessellator = tessellator;
        this.buffer = tessellator.getWorldRenderer();
    }

    public void begin(int i, VertexFormat positionTex) {
        buffer.begin(i, positionTex);
    }

    public WorldRenderer pos(float x, float y, double z) {
        buffer.pos(x, y, z);
        return buffer;
    }

    public WorldRenderer pos(double x, double y, double z) {
        buffer.pos(x, y, z);
        return buffer;
    }

    public void color(int r, int g, int b, int a) {
        buffer.color(r, g, b, a);
    }

}
