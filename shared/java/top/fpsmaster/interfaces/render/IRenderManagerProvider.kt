package top.fpsmaster.interfaces.render

import top.fpsmaster.interfaces.IProvider

interface IRenderManagerProvider : IProvider {
    fun renderPosX(): Double
    fun renderPosY(): Double
    fun renderPosZ(): Double
}