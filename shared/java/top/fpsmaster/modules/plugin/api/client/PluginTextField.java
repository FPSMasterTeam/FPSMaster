package top.fpsmaster.modules.plugin.api.client;

import top.fpsmaster.FPSMaster;
import top.fpsmaster.font.impl.UFontRenderer;
import top.fpsmaster.ui.common.TextField;

public class PluginTextField {
    TextField textField;

    public PluginTextField(int size, String placeHolder, int color, int fontColor, int maxLength) {
        assert FPSMaster.INSTANCE.fontManager != null;
        textField = new TextField(FPSMaster.INSTANCE.fontManager.getFont(size), placeHolder, color, fontColor, maxLength);
    }

    public PluginTextField(int size, boolean hideContent, String placeHolder, int color, int fontColor, int maxLength) {
        assert FPSMaster.INSTANCE.fontManager != null;
        textField = new TextField(FPSMaster.INSTANCE.fontManager.getFont(size), hideContent, placeHolder, color, fontColor, maxLength);
    }

    public void drawTextBox(float x, float y, float width, float height) {
        textField.drawTextBox(x, y, width, height);
    }

    public String getContent() {
        return textField.text;
    }

    public void setContent(String text) {
        textField.text = text;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void textboxKeyTyped(char typedChar, int keyCode) {
        textField.textboxKeyTyped(typedChar, keyCode);
    }
}
