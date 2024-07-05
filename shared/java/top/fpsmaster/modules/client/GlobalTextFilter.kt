package top.fpsmaster.modules.client

import top.fpsmaster.features.impl.utility.IRC
import top.fpsmaster.features.impl.utility.NameProtect
import top.fpsmaster.wrapper.TextFormattingProvider

object GlobalTextFilter {
    @JvmStatic
    @Synchronized
    fun filter(text: String): String {
        if (!IRC.using || !IRC.showMates.value) {
            return NameProtect.filter(text)
        }

        var result = StringBuilder(text)
        for (s in PlayerManager.clientMates) {
            if (!result.contains(s.key)) continue

            val replacement = if (s.value.username.isNotEmpty() && s.value.rank.isNotEmpty()) {
                "${TextFormattingProvider.getReset()}[${s.value.rank}${TextFormattingProvider.getReset()}]${s.key}${TextFormattingProvider.getGray()}(${s.value.username})${TextFormattingProvider.getReset()}"
            } else {
                "${TextFormattingProvider.getGray()}[offline]${TextFormattingProvider.getReset()}${s.key}${TextFormattingProvider.getReset()}"
            }

            val start = result.indexOf(s.key)
            result.replace(start, start + s.key.length, replacement)
        }

        result = StringBuilder(NameProtect.filter(result.toString()))
        return result.toString()
    }
}
