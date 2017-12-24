package xyz.sethy.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.HCFProfile;
import xyz.sethy.hcfactions.goose.GooseTicker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class PlayerRespawnListener implements Listener {

    private final List<UUID> joinNextForRevive;

    public PlayerRespawnListener() {
        this.joinNextForRevive = new ArrayList<>();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(event.getPlayer().getUniqueId());
        if (!this.joinNextForRevive.contains(event.getPlayer().getUniqueId()) && (profile.getDeathbanTime() - System.currentTimeMillis()) > 0L) {
            StringBuilder message = new StringBuilder();
            message.append("&cYou have died!").append("\n").append("&cYour death-ban expires in ").append(GooseTicker.formatTime(profile.getDeathbanTime() - System.currentTimeMillis())).append(".\n");
            if (profile.getLives() < 0)
                message.append("&6You do not have any lives, You can purchase more at https://store.purix.us/").append("\n");
            else {
                message.append("&cYou can use a life to rejoin the server, and skip your death-ban.").append("\n");
                this.joinNextForRevive.add(event.getPlayer().getUniqueId());
            }
            message.append("&bFor more information visit https://purix.us/deathbans");
            event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', message.toString()));
        }

        if(this.joinNextForRevive.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().teleport(Main.getInstance().getSpawn());
            profile.setPlayTimeSinceLastDeath(0L);
            profile.setDeathbanTime(0L);
            HCFAPI.getRedisProfileDAO().update((HCFProfile) profile);
            this.joinNextForRevive.remove(event.getPlayer().getUniqueId());
        }
    }
}
