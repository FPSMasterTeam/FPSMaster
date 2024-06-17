package top.fpsmaster.interfaces.packets


interface IPacketPlayerList:IPacket {
    fun isActionJoin(p: Any): Boolean
    fun isActionLeave(p: Any): Boolean
    fun getEntries(p: Any): List<IAddPlayerData>?
}