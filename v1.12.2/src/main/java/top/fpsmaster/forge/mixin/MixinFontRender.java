package top.fpsmaster.forge.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.modules.client.GlobalTextFilter;
import top.fpsmaster.features.impl.utility.NameProtect;

@Mixin(FontRenderer.class)
public abstract class MixinFontRender {
    @Shadow
    protected abstract void resetStyles();

    @Shadow
    protected abstract int renderString(String text, float x, float y, int color, boolean dropShadow);



    /**
     * @author SuperSkidder
     * @reason NameProtect
     */
    @Overwrite
    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        text = GlobalTextFilter.filter(text);
        GlStateManager.enableAlpha();
        this.resetStyles();
        int i;
        if (dropShadow) {
            i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
            i = Math.max(i, this.renderString(text, x, y, color, false));
        } else {
            i = this.renderString(text, x, y, color, false);
        }

        return i;
    }


    @Shadow
    public abstract int getCharWidth(char character);

    /**
     * @author SuperSkidder
     * @reason NameProtect
     */
    @Overwrite
    public int getStringWidth(String text) {
        text = GlobalTextFilter.filter(text);
        if (text == null) {
            return 0;
        } else {
            int i = 0;
            boolean flag = false;

            for(int j = 0; j < text.length(); ++j) {
                char c0 = text.charAt(j);
                int k = this.getCharWidth(c0);
                if (k < 0 && j < text.length() - 1) {
                    ++j;
                    c0 = text.charAt(j);
                    if (c0 != 'l' && c0 != 'L') {
                        if (c0 == 'r' || c0 == 'R') {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;
                if (flag && k > 0) {
                    ++i;
                }
            }

            return i;
        }
    }
}
