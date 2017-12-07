package xyz.sethy.hcfactions.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

public class BalanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can execute this command."));
            return true;
        }
        Player player = (Player) sender;
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        profile.sendMessage("&aBalance:&f $" + profile.getBalance());
        return false;
    }
}
