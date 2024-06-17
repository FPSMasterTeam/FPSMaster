package top.fpsmaster.wrapper.renderEngine.bufferbuilder;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class WrapperBufferBuilder {
    private final BufferBuilder buffer;
    private final Tessellator tessellator;

    public WrapperBufferBuilder(Tessellator tessellator) {
        this.tessellator = tessellator;
        this.buffer = tessellator.getBuffer();
    }

    public void begin(int i, VertexFormat positionTex) {
        buffer.begin(i, positionTex);
    }

    public BufferBuilder pos(float x, float y, double z) {
        buffer.pos(x, y, z);
        return buffer;
    }

    public BufferBuilder pos(double x, double y, double z) {
        buffer.pos(x, y, z);
        return buffer;
    }

    public void color(int r, int g, int b, int a) {
        buffer.color(r, g, b, a);
    }

}
