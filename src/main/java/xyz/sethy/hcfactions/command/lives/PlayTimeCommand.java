package xyz.sethy.hcfactions.command.lives;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.goose.GooseTicker;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class PlayTimeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can execute this command."));
            return true;
        }
        Player player = (Player) sender;
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + profile.getName() + "'s&e Play Times:"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &aTotal Play Time:&e " + GooseTicker.formatTime(profile.getTotalPlayTime())));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &aTime since last death:&e " + GooseTicker.formatTime(profile.getPlayTimeSinceLastDeath())));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &aThis session:&e " + GooseTicker.formatTime(profile.getOnlineFor())));
        return false;
    }
}
