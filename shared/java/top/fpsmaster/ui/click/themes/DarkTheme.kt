package top.fpsmaster.ui.click.themes

import java.awt.Color

class DarkTheme : Theme {
    override val background: Color
        get() = Color(39, 39, 39)

    override val frontBackground: Color
        get() = Color(58, 58, 58)

    override val typeSelectionBackground: Color
        get() = Color(70, 70, 70)

    override val primary: Color
        get() = Color(113, 127, 254)

    override val primaryGradientLT: Color
        get() = Color(77, 100, 255)

    override val primaryGradientRB: Color
        get() = Color(108, 113, 255)

    override val textColorTitle: Color
        get() = Color(234, 234, 234)

    override val textColorDescription: Color
        get() = Color(162, 162, 162)

    override val categoryTextSelected: Color
        get() = Color(234, 234, 234)

    override val categoryText: Color
        get() = Color(173, 173, 173)

    override val logo: Color
        get() = Color(255, 255, 255)

    override val drag: Color
        get() = Color(200, 200, 200)

    override val dragHovered: Color
        get() = Color(255, 255, 255)

    override val moduleEnabled: Color
        get() = Color(66, 66, 66)

    override val moduleDisabled: Color
        get() = Color(40, 40, 40)

    override val moduleBorder: Color
        get() = Color(200, 200, 200)

    override val moduleTextEnabled: Color
        get() = Color(255, 255, 255)

    override val moduleTextDisabled: Color
        get() = Color(156, 156, 156)

    override val checkboxBox: Color
        get() = Color(71, 71, 71)

    override val checkboxHover: Color
        get() = Color(129, 132, 255)

    override val textNumber: Color
        get() = Color(128, 128, 128)

    override val modeBox: Color
        get() = Color(52, 52, 52)

    override val modeBoxBorder: Color
        get() = Color(255, 255, 255, 50)

    override val textboxEnabled: Color
        get() = Color(58, 58, 58)

    override val textboxDisabled: Color
        get() = Color(30, 30, 30)

    override val textboxFocus: Color
        get() = Color(70, 70, 70)

    override val textboxHover: Color
        get() = Color(64, 64, 64)

    override val textColorDescriptionHover: Color
        get() = Color(182, 182, 182)
    override val musicBlank: Color
        get() = Color(200, 200, 200, 255)
    override val buttonText: Color
        get() = Color(255, 255, 255)
}
