package top.fpsmaster.modules.plugin

import top.fpsmaster.event.EventDispatcher.registerListener
import top.fpsmaster.event.Subscribe
import top.fpsmaster.event.events.EventJoinServer
import top.fpsmaster.modules.logger.Logger
import top.fpsmaster.modules.plugin.api.Plugin
import top.fpsmaster.modules.plugin.api.annotations.PluginAnnotation
import top.fpsmaster.utils.os.FileUtils
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

class PluginManager {
    fun init() {
        // load plugins
        loadPlugins(FileUtils.plugins.absolutePath)
        registerListener(this)
    }

    @Subscribe
    fun onJoin(e: EventJoinServer) {
        for (plugin in plugins) {
            e.cancel = plugin.onJoin(e.serverId)
        }
    }

    private fun loadPlugins(pluginsDirPath: String?) {
        val pluginsDir = File(pluginsDirPath)
        if (pluginsDir.exists()) pluginsDir.mkdirs()
        val pluginFiles = pluginsDir.listFiles { _: File?, name: String -> name.endsWith(".jar") } ?: return
        for (jarFile in pluginFiles) {
            try {
                // Load the JAR file and scan for classes
                val pluginClasses = findPluginClasses(jarFile)

                // Create instances and initialize plugins
                for (cls in pluginClasses) {
                    val plugin = cls!!.getDeclaredConstructor().newInstance() as Plugin
                    plugins.add(plugin)
                    plugin.init()
                    Logger.info("Plugin loaded: " + cls.name)
                }
            } catch (e: Throwable) {
                Logger.error("Failed to load plugin: " + jarFile.name)
            }
        }
    }

    @Throws(Exception::class)
    private fun findPluginClasses(jarFile: File): List<Class<*>?> {
        val classes: MutableList<Class<*>?> = ArrayList()
        val jar = JarFile(jarFile)
        val urls = arrayOf(URL("jar:file:" + jarFile.absolutePath + "!/"))
        val classLoader = URLClassLoader(urls, javaClass.classLoader)
        Thread.currentThread().contextClassLoader = classLoader
        // Iterate over jar entries and look for classes with @PluginAnnotation
        jar.stream().forEach { jarEntry: JarEntry ->
            if (jarEntry.name.endsWith(".class")) {
                val className = jarEntry.name.replace('/', '.').replace(".class", "")
                var cls: Class<*>? = null
                cls = try {
                    classLoader.loadClass(className)
                } catch (e: ClassNotFoundException) {
                    throw RuntimeException(e)
                }
                if (cls?.isAnnotationPresent(PluginAnnotation::class.java)!!) {
                    classes.add(cls)
                }
            }
        }
        return classes
    }

    fun reload() {
        plugins.clear()
        loadPlugins(FileUtils.plugins.absolutePath)
    }
    companion object {
        var plugins = ArrayList<Plugin>()
    }
}
