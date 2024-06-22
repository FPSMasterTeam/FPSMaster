package top.fpsmaster.ui.click.themes

import java.awt.Color

interface Theme {
    val background: Color

    val frontBackground: Color

    val typeSelectionBackground: Color

    val primary: Color

    val primaryGradientLT: Color

    val primaryGradientRB: Color

    val textColorTitle: Color

    val textColorDescription: Color

    val categoryTextSelected: Color

    val categoryText: Color

    val logo: Color

    val drag: Color

    val dragHovered: Color

    val moduleEnabled: Color

    val moduleDisabled: Color

    val moduleBorder: Color

    val moduleTextEnabled: Color

    val moduleTextDisabled: Color

    val checkboxBox: Color

    val checkboxHover: Color

    val textNumber: Color

    val modeBox: Color

    val modeBoxBorder: Color

    val textboxEnabled: Color

    val textboxDisabled: Color

    val textboxFocus: Color

    val textboxHover: Color

    val textColorDescriptionHover: Color
    val musicBlank: Color

    val buttonText: Color
}
