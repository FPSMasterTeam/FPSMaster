package top.fpsmaster.features.command.impl.dev

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.command.Command
import top.fpsmaster.modules.plugin.PluginManager
import top.fpsmaster.utils.Utility

class Plugins: Command("plugins"){
    override fun execute(args: Array<String>) {
        if (args.isEmpty())
            Utility.sendClientMessage("Usage: #plugins <list|reload>")
        else
            if (args[0] == "list"){
                for (plugin in PluginManager.plugins) {
                    Utility.sendClientMessage(plugin.javaClass.simpleName)
                }
            }else if (args[0] == "reload"){
                FPSMaster.INSTANCE.plugins!!.reload()
            }else{
                Utility.sendClientMessage("Usage: #plugins <list|reload>")
            }
    }
}