package top.fpsmaster.modules.music

abstract class AbstractMusic {
    // stuff to display
    @JvmField
    var name: String? = null
    @JvmField
    var author: String? = null
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
