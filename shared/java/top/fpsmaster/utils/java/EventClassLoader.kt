package top.fpsmaster.utils.java

import java.util.Collections
import java.util.WeakHashMap

object EventClassLoader {
    private val loaders: MutableMap<ClassLoader, GeneratedClassLoader> = Collections.synchronizedMap(WeakHashMap())

    fun defineClass(parentLoader: ClassLoader, name: String, data: ByteArray): Class<*> {
        val loader = loaders.computeIfAbsent(parentLoader) { GeneratedClassLoader(it) }
        synchronized(loader.getClassLoadingLock(name)) {
            val clazz = loader.define(name, data)
            assert(clazz.name == name)
            return clazz
        }
    }

    private class GeneratedClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        init {
            ClassLoader.registerAsParallelCapable()
        }

        fun define(name: String, data: ByteArray): Class<*> {
            synchronized(getClassLoadingLock(name)) {
                assert(!hasClass(name))
                val clazz = defineClass(name, data, 0, data.size)
                resolveClass(clazz)
                return clazz
            }
        }

        public override fun getClassLoadingLock(name: String): Any {
            return super.getClassLoadingLock(name)
        }

        fun hasClass(name: String): Boolean {
            synchronized(getClassLoadingLock(name)) {
                return try {
                    Class.forName(name)
                    true
                } catch (e: ClassNotFoundException) {
                    false
                }
            }
        }
    }
}
