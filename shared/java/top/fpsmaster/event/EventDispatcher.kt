package top.fpsmaster.event

import top.fpsmaster.modules.logger.Logger
import java.util.concurrent.CopyOnWriteArrayList

object EventDispatcher {
    private val eventListeners: MutableMap<Class<out Event>, MutableList<Handler>> = HashMap()

    @JvmStatic
    fun registerListener(listener: Any) {
        val methods = listener.javaClass.declaredMethods
        for (method in methods) {
            if (method.isAnnotationPresent(Subscribe::class.java) && method.parameterCount == 1) {
                val parameterType = method.parameterTypes[0]
                if (Event::class.java.isAssignableFrom(parameterType)) {
                    val eventType = parameterType as Class<out Event>
                    val listeners = eventListeners.computeIfAbsent(eventType) { _: Class<out Event>? -> CopyOnWriteArrayList() }
//                    listeners.add(ReflectHandler(listener, method))
                    listeners.add(LambdaHandler(listener, method))
                }
            }
        }
    }

    @JvmStatic
    fun unregisterListener(listener: Any) {
        for (listeners in eventListeners.values) {
            listeners.removeIf { eventListener: Handler -> eventListener.listener.javaClass === listener.javaClass }
        }
    }

    @JvmStatic
    fun dispatchEvent(event: Event) {
        val listeners: List<Handler>? = eventListeners[event.javaClass]
        if (listeners != null) {
            for (listener in listeners) {
                try {
                    listener.invoke(event)
                } catch (e: Exception) {
                    Logger.warn("Failed to dispatch event " + event.javaClass.simpleName + " to listener " + listener.getLog())
                    e.printStackTrace()
                }
            }
        }
    }
}
