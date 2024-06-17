package top.fpsmaster.wrapper.scoreboard;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.Objects;

public class WrapperScoreboard {
    private final Scoreboard scoreboard;

    public WrapperScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public ScorePlayerTeam getPlayersTeam(String name) {
        return scoreboard.getPlayersTeam(name);
    }

    public TextFormatting getPlayersTeamColor(String name) {
        return Objects.requireNonNull(scoreboard.getPlayersTeam(name)).getColor();
    }

    public int getPlayersTeamColorIndex(String name) {
        return Objects.requireNonNull(scoreboard.getPlayersTeam(name)).getColor().getColorIndex();
    }

    public ScoreObjective getObjectiveInDisplaySlot(int i) {
        return scoreboard.getObjectiveInDisplaySlot(i);
    }

    public Collection<Score> getSortedScores(ScoreObjective objective) {
        return scoreboard.getSortedScores(objective);
    }
}
