package top.fpsmaster.ui.screens.mainmenu;

import net.minecraft.util.ResourceLocation;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.utils.math.animation.AnimationUtils;
import top.fpsmaster.utils.render.Render2DUtils;

import java.awt.*;


public class GuiButton {
    public String text;
    public float x, y, width, height;
    public Runnable runnable;
    double alpha = 100.0;

    public GuiButton(String text, Runnable runnable) {
        this.text = text;
        this.runnable = runnable;
    }

    public void render(float x, float y, float width, float height, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (Render2DUtils.isHovered(x, y, width, height, (int) mouseX, (int) mouseY)) {
            alpha = AnimationUtils.base(alpha, 200.0, 0.1);
        } else {
            alpha = AnimationUtils.base(alpha, 100.0, 0.1);
        }
        Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, new Color(0, 0, 0, Render2DUtils.limit(alpha)).getRGB());
        if (!text.equals("settings")) {
            FPSMaster.fontManager.s18.drawCenteredString(FPSMaster.i18n.get(text), x + width / 2, y + height / 2 - 6, FPSMaster.theme.getButtonText().getRGB());
        } else {
            Render2DUtils.drawImage(new ResourceLocation("client/gui/screen/settings.png"), x + width / 2 - 6, y + height / 2 - 6, 12, 12, FPSMaster.theme.getTextColorTitle().getRGB());
        }
    }

    public void mouseClick(float mouseX, float mouseY, int btn) {
        if (Render2DUtils.isHovered(x, y, width, height, (int) mouseX, (int) mouseY) && btn == 0) {
            runnable.run();
        }
    }
}
