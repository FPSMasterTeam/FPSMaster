package top.fpsmaster.event.events

import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.util.ResourceLocation
import top.fpsmaster.event.CancelableEvent

class EventCapeLoading(var playerName: String, var player: AbstractClientPlayer) : CancelableEvent() {
    companion object{
        val capeCache = mutableMapOf<String, ResourceLocation>()
    }

    fun setCachedCape(cape: String) {
        if (capeCache.containsKey(cape)) {
            this.cape = capeCache[cape]
        } else {
            this.cape = ResourceLocation(cape)
            capeCache[cape] = this.cape!!
        }
    }

    var cape:ResourceLocation? = null
}
