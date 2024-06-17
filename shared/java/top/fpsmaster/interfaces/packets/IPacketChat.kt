package top.fpsmaster.interfaces.packets

interface IPacketChat:IPacket {
    fun getUnformattedText(packet: Any): String
    fun getType(p: Any): Int
    fun appendTranslation(p: Any)
}