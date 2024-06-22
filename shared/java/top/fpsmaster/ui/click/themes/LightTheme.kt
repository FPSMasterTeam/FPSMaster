package top.fpsmaster.ui.click.themes

import java.awt.Color

class LightTheme : Theme {
    override val background: Color
        get() = Color(252, 252, 252)

    override val frontBackground: Color
        get() = Color(240, 240, 240)

    override val typeSelectionBackground: Color
        get() = Color(202, 202, 202)

    override val primary: Color
        get() = Color(113, 127, 254)

    override val primaryGradientLT: Color
        get() = Color(77, 100, 255)

    override val primaryGradientRB: Color
        get() = Color(108, 113, 255)

    override val textColorTitle: Color
        get() = Color(61, 61, 61)

    override val textColorDescription: Color
        get() = Color(156, 156, 156)

    override val categoryTextSelected: Color
        get() = Color(255, 255, 255)

    override val categoryText: Color
        get() = Color(61, 61, 61)

    override val logo: Color
        get() = Color(101, 109, 255)

    override val drag: Color
        get() = Color(192, 192, 192)

    override val dragHovered: Color
        get() = Color(172, 172, 172)

    override val moduleEnabled: Color
        get() = Color(255, 255, 255)

    override val moduleDisabled: Color
        get() = Color(240, 240, 240)

    override val moduleBorder: Color
        get() = Color(192, 192, 192)

    override val moduleTextEnabled: Color
        get() = Color(61, 61, 61)

    override val moduleTextDisabled: Color
        get() = Color(61, 61, 61)

    override val checkboxBox: Color
        get() = Color(235, 235, 235)

    override val checkboxHover: Color
        get() = Color(109, 112, 255)

    override val textNumber: Color
        get() = Color(161, 161, 161)

    override val modeBox: Color
        get() = Color(242, 242, 242)

    override val modeBoxBorder: Color
        get() = Color(223, 223, 223)

    override val textboxEnabled: Color
        get() = Color(237, 237, 237)

    override val textboxDisabled: Color
        get() = Color(147, 147, 147)

    override val textboxFocus: Color
        get() = Color(247, 247, 247)

    override val textboxHover: Color
        get() = Color(255, 255, 255)

    override val textColorDescriptionHover: Color
        get() = Color(73, 73, 73)

    override val musicBlank: Color
        get() = Color(100, 100, 100, 255)

    override val buttonText: Color
        get() = Color(255, 255, 255)
}
