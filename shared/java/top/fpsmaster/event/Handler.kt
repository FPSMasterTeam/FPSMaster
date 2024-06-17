package top.fpsmaster.event

import java.lang.reflect.Method

abstract class Handler(val listener: Any, val method: Method) {
    abstract fun invoke(event: Event)

    abstract fun getLog() :String
}