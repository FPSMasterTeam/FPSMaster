package top.fpsmaster.event

import java.lang.reflect.Method

class ReflectHandler(listener: Class<out Event>, method: Method) : Handler(listener, method) {
    override fun invoke(event: Event) {
        method.invoke(listener,event)
    }

    override fun getLog(): String {
        return listener.javaClass.simpleName + " -> " + method.name
    }
}
