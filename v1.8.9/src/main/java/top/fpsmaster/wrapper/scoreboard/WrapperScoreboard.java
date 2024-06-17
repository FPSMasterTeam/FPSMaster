package top.fpsmaster.wrapper.scoreboard;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

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

    public EnumChatFormatting getPlayersTeamColor(String name) {
        return Objects.requireNonNull(scoreboard.getPlayersTeam(name)).getChatFormat();
    }

    public int getPlayersTeamColorIndex(String name) {
        return Objects.requireNonNull(scoreboard.getPlayersTeam(name)).getChatFormat().getColorIndex();
    }

    public ScoreObjective getObjectiveInDisplaySlot(int i) {
        return scoreboard.getObjectiveInDisplaySlot(i);
    }

    public Collection<Score> getSortedScores(ScoreObjective objective) {
        return scoreboard.getSortedScores(objective);
    }
}
