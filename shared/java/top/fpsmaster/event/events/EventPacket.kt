package top.fpsmaster.event.events

import net.minecraft.network.Packet
import top.fpsmaster.event.CancelableEvent

class EventPacket(@JvmField var type: PacketType, @JvmField var packet: Packet<*>) : CancelableEvent() {
    enum class PacketType {
        SEND,
        RECEIVE
    }
}
