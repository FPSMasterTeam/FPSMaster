package top.fpsmaster.interfaces.game

import net.minecraft.client.settings.KeyBinding
import top.fpsmaster.interfaces.IProvider

interface IGameSettings: IProvider {
    fun setKeyPress(key: KeyBinding, value: Boolean)
}