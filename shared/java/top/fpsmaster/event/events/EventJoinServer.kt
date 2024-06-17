package top.fpsmaster.event.events

import top.fpsmaster.event.Event

class EventJoinServer(val serverId: String) : Event {
    var cancel: Boolean = false
}