package top.fpsmaster.ui.custom.impl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import top.fpsmaster.features.impl.interfaces.InventoryDisplay
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.utils.Utility
import top.fpsmaster.interfaces.ProviderManager

class InventoryDisplayComponent : Component(InventoryDisplay::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        drawRect(x - 2, y, 164f, 64f, mod.backgroundColor.color)
        var count = 0
        var count2 = 0
        var linecount = 0
        for (slot in ProviderManager.mcProvider.getPlayer()!!.inventoryContainer.inventorySlots) {
            count2++
            if (count2 <= 9 || count2 > 36) {
                continue
            }
            if (slot.stack != null) {
                val itemStack = slot.stack
                val x1 = (x + count * 18).toInt()
                val y1 = y.toInt() + linecount * 20
                RenderHelper.enableGUIStandardItemLighting()
                Utility.mc.renderItem.renderItemAndEffectIntoGUI(itemStack, x1, y1)
                Utility.mc.renderItem.renderItemOverlays(ProviderManager.mcProvider.getFontRenderer(), itemStack, x1, y1)
                RenderHelper.disableStandardItemLighting()
                GlStateManager.enableAlpha()
                GlStateManager.disableCull()
                GlStateManager.disableBlend()
                GlStateManager.disableLighting()
                GlStateManager.clear(256)
            }
            count++
            if (count >= 9) {
                count = 0
                linecount++
            }
        }
        width = 164f
        height = 64f
    }
}
