package top.fpsmaster.features.impl.utility

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.ui.screens.quickMessage.GuiQuickMessages

class QuickMessage:Module("QuickMessage",Category.Utility) {
    override fun onEnable() {
        super.onEnable()
        mc.displayGuiScreen(GuiQuickMessages())
        set(false)
    }

}