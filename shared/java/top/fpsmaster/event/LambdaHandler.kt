package top.fpsmaster.event

import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

class LambdaHandler(listener: Any, method: Method) : Handler(listener, method) {
    /**
     * Caches dynamic handlers to avoid recreation.
     */
    /**
     * The dynamically generated lambda object that invokes this handler's listener.
     */
    private var handler: DynamicHandler? = null
    private val handlerCache = ConcurrentHashMap<Method, DynamicHandler>()

    init {
        // Lets java ignore some security checks (listeners should always be public)
        method.isAccessible = true
        // Make sure cache doesn't already have a handler for this listener
        if (handlerCache.containsKey(method)) handler = handlerCache[method] else {
            // Get lookup instance
            val lookup = MethodHandles.lookup()
            // Check method modifiers for static
            val isStatic = Modifier.isStatic(method.modifiers)
            // Create methodtype for invoking the methodhandle
            val targetSignature = MethodType.methodType(DynamicHandler::class.java)
            // Generate callsite
            val callSite = LambdaMetafactory.metafactory(
                lookup,  // The lookup instance to use
                "invoke",  // The name of the method to implement
                if (isStatic) targetSignature else targetSignature.appendParameterTypes(listener.javaClass),  // The signature for .invoke()
                MethodType.methodType(Void.TYPE, Event::class.java),  // The method signature to implement
                lookup.unreflect(method),  // Method to invoke when called
                MethodType.methodType(Void.TYPE, method.parameterTypes[0]) // Signature that is enforced at runtime
            )
            // Get target to invoke
            val target = callSite.target
            // Invoke on the object if not static
            handler = (if (isStatic) target.invoke() else target.invoke(listener)) as DynamicHandler
            // Cache this dynamic handler
            handlerCache[method] = handler!!
        }
    }

    override fun invoke(event: Event) {
        this.handler!!.invoke(event)
    }

    override fun getLog(): String {
        return listener.javaClass.simpleName + " -> " + method.name
    }
}