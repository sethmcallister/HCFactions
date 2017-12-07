package xyz.sethy.hcfactions.goose;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.LinkedList;

public class GooseScoreboard {
    private LinkedList<ScoreboardText> texts = new LinkedList<>();
    private Scoreboard scoreboard;
    private Objective objective;
    private String tag = "PlaceHolder";
    private int lastSentCount = -1;

    public GooseScoreboard(Scoreboard scoreBoard) {
        this.scoreboard = scoreBoard;
        this.objective = getOrCreateObjective(this.tag);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }


    public GooseScoreboard(Scoreboard scoreBoard, String title) {
        Preconditions.checkState(title.length() <= 32, "title can not be more than 32");

        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.scoreboard = scoreBoard;
        this.objective = getOrCreateObjective(this.tag);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void add(String left, String right) {
        Preconditions.checkState(left.length() <= 16, "left can not be more than 16");
        Preconditions.checkState(right.length() <= 16, "right can not be more than 16");
        this.texts.add(new ScoreboardText(left, right));
    }

    public void add(String left, String middle, String right) {
        Preconditions.checkState(left.length() <= 16, "left can not be more than 16");
        Preconditions.checkState(middle.length() <= 16, "middle can not be more than 16");
        Preconditions.checkState(right.length() <= 16, "right can not be more than 16");
        this.texts.add(new ScoreboardText(left, middle, right));
    }

    public void set(int index, String left, String right) {
        Preconditions.checkState(left.length() <= 16, "left can not be more than 16");
        Preconditions.checkState(right.length() <= 16, "right can not be more than 16");
        this.texts.set(index, new ScoreboardText(left, right));
    }

    public void clear() {
        this.texts.clear();
    }

    public void remove(int index) {
        String name = getNameForIndex(index);
        this.scoreboard.resetScores(name);
        Team team = getOrCreateTeam(ChatColor.stripColor(this.tag) + index, index);
        team.unregister();
    }

    public void update() {
        Collections.reverse(this.texts);
        for (int i = 0; i < this.texts.size(); i++) {
            Team team = getOrCreateTeam(ChatColor.stripColor(this.tag) + i, i);
            ScoreboardText str = this.texts.get(this.texts.size() - i - 1);
            team.setPrefix(str.getLeft());
            if (str.getMiddle() != null) {
                team.setDisplayName(str.getMiddle());
            }
            team.setSuffix(str.getRight());
            this.objective.getScore(getNameForIndex(i)).setScore(15 - i);
        }
        if (this.lastSentCount != -1) {
            int sentCount = this.texts.size();
            for (int i = 0; i < this.lastSentCount - sentCount; i++) {
                remove(sentCount + i);
            }
        }
        this.lastSentCount = this.texts.size();
    }

    public Team getOrCreateTeam(String team, int i) {
        Team value = this.scoreboard.getTeam(team);
        if (value == null) {
            value = this.scoreboard.registerNewTeam(team);
            value.addEntry(getNameForIndex(i));
        }
        return value;
    }

    public Objective getOrCreateObjective(String objective) {
        Objective value = this.scoreboard.getObjective("dummyhubobj");
        if (value == null) {
            value = this.scoreboard.registerNewObjective("dummyhubobj", "dummy");
        }
        value.setDisplayName(objective);
        return value;
    }

    public String getNameForIndex(int index) {
        return ChatColor.values()[index].toString() + ChatColor.RESET;
    }

    public static class ScoreboardText {
        private String left;
        private String middle;
        private String right;

        public ScoreboardText(String left, String middle, String right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public ScoreboardText(String left, String right) {
            this.left = left;
            this.right = right;
        }


        public String getLeft() {
            return this.left;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public String getRight() {
            return this.right;
        }

        public void setRight(String right) {
            this.right = right;
        }

        public String getMiddle() {
            return this.middle;
        }

        public void setMiddle(String right) {
            this.middle = middle;
        }
    }
}
