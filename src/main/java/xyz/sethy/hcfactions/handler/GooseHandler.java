package xyz.sethy.hcfactions.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.goose.GooseScoreboard;

import java.util.concurrent.ConcurrentHashMap;

public class GooseHandler implements Listener {
    private final ConcurrentHashMap<Player, GooseScoreboard> scoreboards;

    public GooseHandler() {
        this.scoreboards = new ConcurrentHashMap<>();
    }

    public GooseScoreboard getScoreboard(Player player) {
        return scoreboards.get(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        event.getPlayer().setScoreboard(scoreboard);

        GooseScoreboard gooseScoreboard = new GooseScoreboard(scoreboard, String.format("&b&lPURIX &c[Map %s]", HCFAPI.getMapName()));
        scoreboards.put(event.getPlayer(), gooseScoreboard);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        scoreboards.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        scoreboards.remove(event.getPlayer());
    }
}
