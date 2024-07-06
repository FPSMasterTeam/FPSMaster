package top.fpsmaster.ui.custom

import top.fpsmaster.features.impl.InterfaceModule
import top.fpsmaster.ui.custom.impl.*
import java.util.function.Consumer

class ComponentsManager {
    var components = ArrayList<Component>()
    var dragLock = ""

    fun init() {
        components.add(FPSDisplayComponent())
        components.add(ArmorDisplayComponent())
        components.add(MusicComponent())
        components.add(ScoreboardComponent())
        components.add(PotionDisplayComponent())
        components.add(CPSDisplayComponent())
        components.add(KeystrokesComponent())
        components.add(ReachDisplayComponent())
        components.add(ComboDisplayComponent())
        components.add(LyricsComponent())
        components.add(InventoryDisplayComponent())
        components.add(TargetHUDComponent())
        components.add(PlayerDisplayComponent())
        components.add(PingDisplayComponent())
        components.add(CoordsDisplayComponent())
        components.add(ModsListComponent())
        components.add(MiniMapComponent())
    }

    fun getComponent(clazz: Class<out InterfaceModule>): Component {
        return components.stream().filter { component: Component -> component.mod.javaClass == clazz }
            .findFirst().orElse(null)
    }

    fun draw(mouseX: Int, mouseY: Int) {
        components.forEach(Consumer { component: Component ->
            if (component.shouldDisplay()) component.display(
                mouseX,
                mouseY
            )
        })
    }
}
