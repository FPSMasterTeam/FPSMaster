package top.fpsmaster.interfaces.game

import top.fpsmaster.interfaces.IProvider

interface ITimerProvider : IProvider {
    fun getRenderPartialTicks(): Float
}