package top.fpsmaster.utils.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import top.fpsmaster.utils.Utility;
import top.fpsmaster.utils.awt.RoundUtil;
import top.fpsmaster.wrapper.renderEngine.bufferbuilder.WrapperBufferBuilder;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Render2DUtils extends Utility {
    public static void drawOptimizedRoundedRect(float x, float y, float width, float height, Color color) {
        drawOptimizedRoundedRect(x, y, width, height, 3, color.getRGB());
    }

    public static void drawOptimizedRoundedBorderRect(float x, float y, float width, float height, float lineWidth, Color color, Color border) {
        drawOptimizedRoundedRect(x - lineWidth, y - lineWidth, width + lineWidth * 2, height + lineWidth * 2, 5, border.getRGB());
        drawOptimizedRoundedRect(x, y, width, height, 3, color.getRGB());
    }

    public static void drawOptimizedRoundedRect(float x, float y, float width, float height, int color) {
        drawOptimizedRoundedRect(x, y, width, height, 3, color);
    }


    public static void drawOptimizedRoundedRect(float x, float y, float width, float height, int radius, int color) {
        if (width < radius * 2 || radius < 1) {
            Render2DUtils.drawRect(x, y, width, height, color);
            return;
        }
        radius = (int) Math.min(Math.min(height, width) / 2, radius);
        RoundUtil.generateRound(radius);
        drawImage(new ResourceLocation("fpsmaster/rounded/" + radius + "/lt.png"), x, y, radius, radius, color);
        drawImage(new ResourceLocation("fpsmaster/rounded/" + radius + "/rt.png"), x + width - radius, y, radius, radius, color);
        drawImage(new ResourceLocation("fpsmaster/rounded/" + radius + "/lb.png"), x, y + height - radius, radius, radius, color);
        drawImage(new ResourceLocation("fpsmaster/rounded/" + radius + "/rb.png"), x + width - radius, y + height - radius, radius, radius, color);
        drawRect(x + radius, y, width - radius * 2, radius, color);
        drawRect(x + radius, y + height - radius, width - radius * 2, radius, color);
        drawRect(x, y + radius, radius, height - radius * 2, color);
        drawRect(x + width - radius, y + radius, radius, height - radius * 2, color);
        drawRect(x + radius, y + radius, width - radius * 2, height - radius * 2, color);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        drawRect(x, y, width, height, color.getRGB());
    }

    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), limit(alpha));
    }

    public static int limit(double i) {
        if (i > 255)
            return 255;
        if (i < 0)
            return 0;
        return (int) i;
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        GlStateManager.disableBlend();

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();

    }

    public static Color intToColor(Integer c) {
        return new Color(c >> 16 & 255, c >> 8 & 255, c & 255, c >> 24 & 255);
    }

    private static void glColor(int color) {
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int alpha = color >> 24 & 255;
        GL11.glColor4f(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
    }

    public static void drawImage(ResourceLocation res, float x, float y, float width, float height, Color color) {
        drawImage(res, x, y, width, height, color.getRGB());
    }

    public static void drawImage(ResourceLocation res, float x, float y, float width, float height, int color) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor(color);
        mc.getTextureManager().bindTexture(res);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WrapperBufferBuilder bufferbuilder = new WrapperBufferBuilder(tessellator);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void doGlScissor(float x, float y, float width, float height) {
        if (mc.currentScreen != null) {
            GL11.glScissor((int) (x * mc.displayWidth / mc.currentScreen.width), (int) (mc.displayHeight - (y + height) * mc.displayHeight / mc.currentScreen.height), (int) (width * mc.displayWidth / mc.currentScreen.width), (int) (height * mc.displayHeight / mc.currentScreen.height));
        }
    }


    public static void drawHue(float x, float y, int width, float height) {
        float hue = 0;
        float increment = 1.0F / height;
        for (int i = 0; i < height; i++) {
            drawRect(x, y + i, width, 1, Color.getHSBColor(hue, 1.0F, 1.0F).getRGB());
            hue += increment;
        }
    }

    public static void drawPlayerHead(EntityPlayer target, float x, float y, int w, int h) {
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8, 8, 8, 8, w, h, 64, 64);
    }
}
