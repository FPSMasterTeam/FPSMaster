package top.fpsmaster.utils.thirdparty.rawinput

import net.minecraft.util.MouseHelper

class RawMouseHelper : MouseHelper() {
    override fun mouseXYChange() {
        deltaX = RawInputMod.dx
        RawInputMod.dx = 0
        deltaY = -RawInputMod.dy
        RawInputMod.dy = 0
    }
}