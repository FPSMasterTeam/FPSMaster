package top.fpsmaster.interfaces.game

import top.fpsmaster.interfaces.IProvider

interface ISkinProvider : IProvider {
    fun updateSkin(name: String?, uuid: String?, skin: String?)
}