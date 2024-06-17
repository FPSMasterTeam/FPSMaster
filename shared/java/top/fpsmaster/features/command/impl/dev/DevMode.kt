package top.fpsmaster.features.command.impl.dev

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.command.Command
import top.fpsmaster.utils.Utility

class DevMode :Command("dev") {
    override fun execute(args: Array<String>) {
        FPSMaster.debug = !FPSMaster.debug
        Utility.sendClientMessage("Dev mode is now ${if(FPSMaster.debug) "enabled" else "disabled"}")
    }
}