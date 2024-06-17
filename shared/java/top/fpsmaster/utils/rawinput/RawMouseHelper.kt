package top.fpsmaster.utils.rawinput

import net.minecraft.client.Minecraft
import net.minecraft.util.MouseHelper

class RawMouseHelper : MouseHelper() {
    override fun mouseXYChange() {
        deltaX = RawInputMod.dx
        RawInputMod.dx = 0
        deltaY = -RawInputMod.dy
        RawInputMod.dy = 0
    }
}