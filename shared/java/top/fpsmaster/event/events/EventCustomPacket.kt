package top.fpsmaster.event.events

import net.minecraft.network.Packet
import top.fpsmaster.event.Event

class EventCustomPacket(var packet: Packet<*>) : Event
