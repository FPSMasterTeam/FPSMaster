package top.fpsmaster.modules.plugin.api.client;

import top.fpsmaster.FPSMaster;

public class FontLoaders {
    public static void drawString(int size, String text, float x, float y, int color, boolean shadow) {
        assert FPSMaster.fontManager != null;
        if (shadow)
            FPSMaster.fontManager.getFont(size).drawStringWithShadow(text, x, y, color);
        else
            FPSMaster.fontManager.getFont(size).drawString(text, x, y, color);
    }

    public static void drawCenteredString(int size, String text, float x, float y, int color) {
        assert FPSMaster.fontManager != null;
        FPSMaster.fontManager.getFont(size).drawCenteredString(text, x, y, color);
    }
}
