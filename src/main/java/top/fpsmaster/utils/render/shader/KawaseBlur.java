package top.fpsmaster.utils.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class KawaseBlur {
    private static final Minecraft mc = Minecraft.getMinecraft();
    // Created on first blur rather than at class-init: the shader constructor links a GL program,
    // so eager fields would make the shutdown release path allocate in a session that never blurred.
    private static ShaderUtil kawaseDown;
    private static ShaderUtil kawaseUp;
    private static Framebuffer framebuffer;
    private static final List<Framebuffer> framebufferList = new ArrayList<>();
    private static int currentIterations = 0;

    /** Upper bound on the half-size FBO chain, so a pathological setting cannot exhaust GPU memory. */
    private static final int MAX_ITERATIONS = 8;

    /** Render thread only, like every other entry point here, so no locking. */
    private static void ensureShaders() {
        if (kawaseDown == null) {
            kawaseDown = new ShaderUtil("blurDown");
            kawaseUp = new ShaderUtil("blurUp");
        }
    }

    public static void setupUniforms(float offset) {
        ensureShaders();
        kawaseDown.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("offset", offset, offset);
    }

    private static void initFramebuffers(float iterations) {
        for (Framebuffer fb : framebufferList) {
            fb.deleteFramebuffer();
        }
        framebufferList.clear();
        if (framebuffer == null) {
            framebuffer = new Framebuffer(1, 1, false);
        }
        framebufferList.add(framebuffer);
        // Dual Kawase: level i is the display size >> i. Every level used to be full resolution, so
        // each pass shaded the whole (on Retina, doubled) screen and the "half-size chain" was not.
        int i = 1;
        while (i <= iterations) {
            Framebuffer fb = new Framebuffer(levelSize(mc.displayWidth, i), levelSize(mc.displayHeight, i), false);
            // Levels are sampled up into larger targets; nearest filtering would show the blocks.
            fb.setFramebufferFilter(GL11.GL_LINEAR);
            framebufferList.add(fb);
            i++;
        }
    }

    private static int levelSize(int full, int level) {
        return Math.max(1, full >> level);
    }

    public static void renderBlur(int iterations, int offset) {
        if (iterations <= 0) {
            return;
        }
        ensureShaders();
        iterations = Math.min(iterations, MAX_ITERATIONS);
        // Rebuild on a size change too, so a resized window cannot leave the half-size chain stale.
        if (currentIterations != iterations
                || framebufferList.isEmpty()
                || (framebufferList.size() > 1
                    && (framebufferList.get(1).framebufferWidth != levelSize(mc.displayWidth, 1)
                        || framebufferList.get(1).framebufferHeight != levelSize(mc.displayHeight, 1)))) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }
        renderFBO(framebufferList.get(1), mc.getFramebuffer().framebufferTexture, kawaseDown, (float) offset);

        // Downsample
        for (int i = 1; i < iterations; i++) {
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, (float) offset);
        }

        // Upsample
        for (int i = iterations; i >= 2; i--) {
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, (float) offset);
        }

        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.bindTexture(framebufferList.get(1).framebufferTexture);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", (float) offset, (float) offset);
        kawaseUp.setUniformf("halfpixel", 0.5f / mc.displayWidth, 0.5f / mc.displayHeight);
        kawaseUp.setUniformi("inTexture", 0);
        ShaderUtil.drawQuads();
        kawaseUp.unload();
    }

    /**
     * Drops the whole half-size chain. The list is rebuilt on the next {@link #renderBlur}, so this
     * is safe at any point; it exists for shutdown, where nothing else would ever free these.
     */
    public static void release() {
        for (Framebuffer fb : framebufferList) {
            fb.deleteFramebuffer();
        }
        framebufferList.clear();
        if (framebuffer != null) {
            // Already deleted above when the chain had been built; deleteFramebuffer is idempotent.
            framebuffer.deleteFramebuffer();
            framebuffer = null;
        }
        currentIterations = 0;
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shader.init();
        GlStateManager.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        // Half a texel of the pass's target, as in the reference dual-filter implementation.
        shader.setUniformf("halfpixel", 0.5f / framebuffer.framebufferWidth, 0.5f / framebuffer.framebufferHeight);
        ShaderUtil.drawQuads();
        shader.unload();
        framebuffer.unbindFramebuffer();
    }
}


