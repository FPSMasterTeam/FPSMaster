package top.fpsmaster.event.events

import top.fpsmaster.event.CancelableEvent

class EventSendChatMessage(@JvmField var msg: String) : CancelableEvent()
