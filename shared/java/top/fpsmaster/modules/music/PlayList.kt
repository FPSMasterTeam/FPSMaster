package top.fpsmaster.modules.music

import top.fpsmaster.FPSMaster
import top.fpsmaster.ui.notification.addNotification
import java.util.*

class PlayList {
    var musics = LinkedList<AbstractMusic>()
    var current = 0
    fun add(music: AbstractMusic) {
        musics.add(music)
    }

    fun play() {
        addNotification(
            FPSMaster.INSTANCE.i18n["notification.music"],
            FPSMaster.INSTANCE.i18n["notification.music.next"].replace(
                "%s",
                MusicPlayer.playList.getCurrent()?.name ?: ""
            ),
            2f
        )
        musics[current].play()
        MusicPlayer.isPlaying = true
    }

    fun getCurrent(): AbstractMusic? {
        return if (musics.size == 0) null else musics[current]
    }

    fun pause() {
        MusicPlayer.pause()
    }

    fun seek(percent: Float) {
        musics[current].seek(percent)
    }

    operator fun next() {
        MusicPlayer.stop()
        if (musics.size == 0)
            return
        current++
        if (current >= musics.size) {
            current = 0
        }
        addNotification(
            FPSMaster.INSTANCE.i18n["notification.music"],
            FPSMaster.INSTANCE.i18n["notification.music.next"].replace(
                "%s",
                MusicPlayer.playList.getCurrent()?.name ?: ""
            ),
            2f
        )
        musics[current].play()
        MusicPlayer.isPlaying = true
    }

    fun previous() {
        MusicPlayer.stop()
        if (musics.size == 0)
            return
        current--
        if (current < 0) {
            current = musics.size - 1
        }
        addNotification(
            FPSMaster.INSTANCE.i18n["notification.music"],
            FPSMaster.INSTANCE.i18n["notification.music.next"].replace(
                "%s",
                MusicPlayer.playList.getCurrent()?.name ?: ""
            ),
            2f
        )
        musics[current].play()
    }

    fun remove(music: AbstractMusic) {
        musics.remove(music)
    }

    fun remove(index: Int) {
        musics.removeAt(index)
    }

    fun clear() {
        musics.clear()
    }

    fun shuffle() {
        musics.shuffle()
    }
}
