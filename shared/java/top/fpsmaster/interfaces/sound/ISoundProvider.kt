package top.fpsmaster.interfaces.sound

interface ISoundProvider {
    fun playLightning(posX: Double, posY: Double, posZ: Double, i: Float, v: Float, b: Boolean)
    fun playExplosion(posX: Double, posY: Double, posZ: Double, i: Float, v: Float, b: Boolean)
    fun playRedStoneBreak(posX: Double, posY: Double, posZ: Double, i: Float, v: Float, b: Boolean)

}