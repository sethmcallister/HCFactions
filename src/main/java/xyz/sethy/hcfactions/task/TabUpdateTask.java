package xyz.sethy.hcfactions.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.UUID;

public class TabUpdateTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers())
            updatePlayer(player);
    }

    private void updatePlayer(Player player) {
        Scoreboard scoreboard;
        if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard())
            scoreboard = player.getScoreboard();
        else
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Team friendly = this.getExistingOrCreateNewTeam("friendly", scoreboard, ChatColor.GREEN);
        Team archerTagged = this.getExistingOrCreateNewTeam("archerTagged", scoreboard, ChatColor.DARK_RED);
        Team ally = this.getExistingOrCreateNewTeam("ally", scoreboard, ChatColor.AQUA);

        Faction faction = HCFAPI.getHCFManager().findByUser(player.getUniqueId());

        friendly.addEntry(player.getName());
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(player1))
                continue;

            if (faction != null) {
                if (faction.getAllMembers().contains(player1.getUniqueId()))
                    friendly.addEntry(player1.getName());

                for (UUID factionId : faction.getAllies()) {
                    Faction faction1 = HCFAPI.getHCFManager().findByUniqueId(factionId);
                    if (faction1.getAllMembers().contains(player1.getUniqueId()))
                        ally.addEntry(player1.getName());
                }
            }

            if (Main.getInstance().getTimerHandler().hasTimer(player1, TimerType.ARCHER_TAG))
                archerTagged.addEntry(player1.getName());
        }

        player.setScoreboard(scoreboard);
    }

    private Team getExistingOrCreateNewTeam(String string, Scoreboard scoreboard, ChatColor prefix) {
        Team toReturn = scoreboard.getTeam(string);
        if (toReturn == null) {
            toReturn = scoreboard.registerNewTeam(string);
            toReturn.setPrefix(String.valueOf(prefix));
            if (string.equalsIgnoreCase("friendly") || string.equalsIgnoreCase("ally"))
                toReturn.setCanSeeFriendlyInvisibles(true);
        }
        return toReturn;
    }
}
