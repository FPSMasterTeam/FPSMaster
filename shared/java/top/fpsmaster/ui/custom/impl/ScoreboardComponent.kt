package top.fpsmaster.ui.custom.impl

import top.fpsmaster.features.impl.interfaces.Scoreboard
import top.fpsmaster.ui.custom.Component
import top.fpsmaster.wrapper.mods.WrapperScoreboard

class ScoreboardComponent : Component(Scoreboard::class.java) {
    override fun draw(x: Float, y: Float) {
        super.draw(x, y)
        val render = WrapperScoreboard.render(this, mod, x, y)
        width = render[0]
        height = render[1]
    }
}
