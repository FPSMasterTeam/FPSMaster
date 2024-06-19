package top.fpsmaster.features.impl.interfaces

import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventMouseClick
import top.fpsmaster.event.events.EventTick
import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.settings.impl.ColorSetting
import java.awt.Color
import java.util.*

class CPSDisplay : InterfaceModule("CPSDisplay", Category.Interface) {
    private var keys = LinkedList<Key>()

    init {
        addSettings(textColor)
        addSettings(rounded, backgroundColor, fontShadow, betterFont, bg, rounded, roundRadius)
    }

    @Subscribe
    fun onClick(e: EventMouseClick) {
        if (e.button == 0) {
            keys.add(Key(0, System.currentTimeMillis()))
        } else if (e.button == 1) {
            keys.add(Key(1, System.currentTimeMillis()))
        }
    }

    @Subscribe
    fun onTick(e: EventTick) {
        lcps = keys.stream().filter { key: Key -> key.key == 0 && System.currentTimeMillis() - key.time < 1000L }
            .count()
        rcps = keys.stream().filter { key: Key -> key.key == 1 && System.currentTimeMillis() - key.time < 1000L }
            .count()
        keys.removeIf { key: Key -> System.currentTimeMillis() - key.time > 1000L }
    }

    companion object {
        @JvmField
        var lcps: Long = 0

        @JvmField
        var rcps: Long = 0

        @JvmField
        var textColor = ColorSetting("TextColor", Color(255, 255, 255))
    }
}

class Key(key: Int, time: Long) {
    var key = 0
    var time: Long = 0

    init {
        this.key = key
        this.time = time
    }
}