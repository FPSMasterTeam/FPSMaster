package top.fpsmaster.ui.click.themes;

import java.awt.Color;

public class LightTheme implements Theme {
    public Color getBackground() {
        return new Color(252,252,252);
    }

    public Color getFrontBackground() {
        return new Color(240,240,240);
    }

    public Color getTypeSelectionBackground() {
        return new Color(202,202,202);
    }

    public Color getPrimary() {
        return new Color(113, 127, 254);
    }

    public Color getPrimaryGradientLT() {
        return new Color(77, 100, 255);
    }

    public Color getPrimaryGradientRB() {
        return new Color(108, 113, 255);
    }

    public Color getTextColorTitle() {
        return new Color(61,61,61);
    }

    public Color getTextColorDescription() {
        return new Color(156,156,156);
    }

    public Color getCategoryTextSelected() {
        return new Color(255,255,255);
    }

    public Color getCategoryText() {
        return new Color(61,61,61);
    }

    public Color getLogo() {
        return new Color(101,109,255);
    }

    public Color getDrag() {
        return new Color(192,192,192);
    }

    public Color getDragHovered() {
        return new Color(172,172,172);
    }

    public Color getModuleEnabled() {
        return new Color(255,255,255);
    }

    public Color getModuleDisabled() {
        return new Color(240,240,240);
    }

    public Color getModuleBorder() {
        return new Color(192,192,192);
    }

    public Color getModuleTextEnabled() {
        return new Color(61,61,61);
    }

    public Color getModuleTextDisabled() {
        return new Color(61,61,61);
    }

    public Color getCheckboxBox() {
        return new Color(235,235,235);
    }

    public Color getCheckboxHover() {
        return new Color(109,112,255);
    }

    public Color getTextNumber() {
        return new Color(161,161,161);
    }

    public Color getModeBox() {
        return new Color(242,242,242);
    }

    public Color getModeBoxBorder() {
        return new Color(223,223,223);
    }

    public Color getTextboxEnabled() {
        return new Color(237,237,237);
    }

    public Color getTextboxDisabled() {
        return new Color(147,147,147);
    }

    public Color getTextboxFocus() {
        return new Color(247,247,247);
    }

    public Color getTextboxHover() {
        return new Color(255, 255, 255);
    }

    public Color getTextColorDescriptionHover() {
        return new Color(73,73,73);
    }

    public Color getMusicBlank() {return new Color(100,100,100,255);}

    @Override
    public Color getButtonText() {
        return new Color(255, 255, 255);
    }
}
