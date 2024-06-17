package top.fpsmaster.modules.plugin.api.client;

import org.jetbrains.annotations.NotNull;
import top.fpsmaster.ui.common.GuiButton;

public class PluginButton extends GuiButton {
    public PluginButton(@NotNull String text, @NotNull Runnable runnable) {
        super(text, runnable);
    }

    public void draw(float x, float y, float width, float height, float mouseX, float mouseY) {
        super.render(x, y, width, height, mouseX, mouseY);
    }

    public void mouseClicked(float mouseX, float mouseY, int btn) {
        super.mouseClick(mouseX, mouseY, btn);
    }
}
