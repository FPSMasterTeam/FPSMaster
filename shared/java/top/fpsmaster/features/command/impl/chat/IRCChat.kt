package top.fpsmaster.features.command.impl.chat

import top.fpsmaster.FPSMaster
import top.fpsmaster.features.command.Command
import top.fpsmaster.utils.Utility

class IRCChat : Command("irc") {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            if (args[0] == "cmd") {
                val sb = StringBuilder()
                for (arg in args.drop(1)) {
                    if (arg == args.last())
                        sb.append(arg)
                    else
                        sb.append("$arg ")
                }
                val message = sb.toString()
                FPSMaster.INSTANCE.wsClient!!.sendCommand(message)
            } else if (args[0] == "dm") {
                val sb = StringBuilder()
                for (arg in args.drop(2)) {
                    if (arg == args.last())
                        sb.append(arg)
                    else
                        sb.append("$arg ")
                }
                val message = sb.toString()
                FPSMaster.INSTANCE.wsClient!!.sendDM(args[1], message)
            } else {
                val sb = StringBuilder()
                for (arg in args) {
                    if (arg == args.last())
                        sb.append(arg)
                    else
                        sb.append("$arg ")
                }
                val message = sb.toString()
                FPSMaster.INSTANCE.wsClient!!.sendMessage(message)
            }
        } else {
            Utility.sendClientMessage("Usage: #irc <message>")
        }
    }
}