package top.fpsmaster.modules.music

abstract class AbstractMusic {
    // stuff to display
    @JvmField
    var name: String = ""
    @JvmField
    var author: String = ""
    @JvmField
    var lyrics: Lyrics? = null
    var tlyrics: Lyrics? = null
    var rlyrics: Lyrics? = null
    abstract fun play()
    fun seek(percent: Float) {
        if (JLayerHelper.clip != null) {
            JLayerHelper.seek(percent)
        }
    }
}
