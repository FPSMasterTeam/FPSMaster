package top.fpsmaster.modules.music

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import top.fpsmaster.FPSMaster
import top.fpsmaster.features.impl.interfaces.MusicOverlay
import top.fpsmaster.modules.music.MusicPlayer.playProgress
import top.fpsmaster.modules.music.netease.Music
import top.fpsmaster.utils.Utility
import top.fpsmaster.utils.math.animation.AnimationUtils.base
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color

object IngameOverlay {
    private var songProgress = 0f
    var smoothCurve: DoubleArray? = DoubleArray(0)

    fun onRender() {
        if (MusicPlayer.playList.getCurrent() != null) {
            val sr = ScaledResolution(Utility.Companion.mc)
            if (!MusicPlayer.getCurve()!!.isEmpty()) {
                val width: Float = ((sr.scaledWidth / 2f - 100) / MusicPlayer.getCurve()!!.size).coerceAtLeast(1f)
                var x = 0f
                if (smoothCurve!!.size != MusicPlayer.getCurve()!!.size) {
                    smoothCurve = MusicPlayer.getCurve()!!
                }
                for (i in 0 until MusicPlayer.getCurve()!!.size) {
                    smoothCurve?.set(i, base(smoothCurve!![i], MusicPlayer.getCurve()?.get(i) ?: return, 0.15).toFloat().toDouble())
                }
                for (musicMagnitude in MusicPlayer.getCurve()!!) {
                    var musicMagnitude: Double = musicMagnitude
                    if (musicMagnitude > 0.1f) {
                        Render2DUtils.drawRect(
                            x,
                            sr.scaledHeight - musicMagnitude.toFloat()*100,
                            width,
                            musicMagnitude.toFloat(),
                            MusicOverlay.color.rGB
                        )
                    }
                    x += width
                    if (x > (sr.scaledWidth / 2f)) break
                }
            }
        }
    }

    fun drawSong(x: Float, y: Float, width: Float, height: Float) {
        val sr = ScaledResolution(Utility.Companion.mc)
        val current = MusicPlayer.playList.getCurrent() as Music
        val s18 = FPSMaster.Companion.fontManager.s18
        Render2DUtils.drawOptimizedRoundedRect(x, y, width, height, Color(0, 0, 0, 180))
        Render2DUtils.drawOptimizedRoundedRect(x, y, songProgress, height, MusicOverlay.progressColor.color)
        songProgress = base(songProgress.toDouble(), (6 + (width - 6) * playProgress).toDouble(), 0.1).toFloat()
        Render2DUtils.drawImage(
            ResourceLocation("music/netease/" + current.id),
            x + 5,
            y + 5,
            height - 10,
            height - 10,
            -1
        )
        FPSMaster.Companion.fontManager.s18.drawString(
            current.name,
            x + 40,
            y + 6,
            FPSMaster.theme.textColorTitle.rgb
        )
        FPSMaster.Companion.fontManager.s16.drawString(
            current.author,
            x + 40,
            y + 18,
            FPSMaster.theme.textColorDescription.rgb
        )
    }
}
