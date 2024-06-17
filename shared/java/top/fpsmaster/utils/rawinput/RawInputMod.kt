package top.fpsmaster.utils.rawinput

import net.java.games.input.Controller
import net.java.games.input.ControllerEnvironment
import net.java.games.input.Mouse
import net.minecraft.client.Minecraft
import net.minecraft.util.MouseHelper

class RawInputMod {
    private var inputThread: Thread? = null
    fun start() {
        try {
            Minecraft.getMinecraft().mouseHelper = RawMouseHelper()
            controllers = ControllerEnvironment.getDefaultEnvironment().controllers
            inputThread = Thread {
                while (true) {
                    var i = 0
                    while (i < controllers.size && mouse == null) {
                        if (controllers[i].type === Controller.Type.MOUSE) {
                            controllers[i].poll()
                            if ((controllers[i] as Mouse).x.pollData.toDouble() != 0.0 || (controllers[i] as Mouse).y.pollData.toDouble() != 0.0) mouse =
                                controllers[i] as Mouse
                        }
                        i++
                    }
                    if (mouse != null) {
                        mouse!!.poll()
                        dx += mouse!!.x.pollData.toInt()
                        dy += mouse!!.y.pollData.toInt()
                        if (Minecraft.getMinecraft().currentScreen != null) {
                            dx = 0
                            dy = 0
                        }
                    }
                    try {
                        Thread.sleep(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            inputThread!!.name = "inputThread"
            inputThread!!.start()
        } catch (e: Exception) {
            // ignored
        }
    }

    fun stop() {
        try {
            if (inputThread!!.isAlive) inputThread!!.interrupt()
            Minecraft.getMinecraft().mouseHelper = MouseHelper()
        } catch (e: Exception) {
            // ignored
        }
    }

    companion object {
        var mouse: Mouse? = null
        lateinit var controllers: Array<Controller>

        // Delta for mouse
        var dx = 0
        var dy = 0
    }
}
