package xyz.sethy.hcfactions.command.economy;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can execute this command."));
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 2) {
            sender.sendMessage("&cUsage: /pay <player> <amount>");
            return true;
        }
        Profile user = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        Profile receiver = HCFAPI.getHCFManager().findProfileByString(args[0]);
        if (receiver == null) {
            user.sendMessage("&cNo player with the name '" + args[0] + "' was found.");
            return true;
        }
        if (!NumberUtils.isNumber(args[1])) {
            sender.sendMessage("&cThe argument '" + args[1] + "' is not a number.");
            return true;
        }
        Integer amount = Integer.parseInt(args[1]);

        Double userBalance = user.getBalance();
        Double receiversBalance = receiver.getBalance();

        if (userBalance < amount) {
            sender.sendMessage("&cYou do not have enough money to do this.");
            return true;
        }
        user.setBalance(userBalance - amount);
        receiver.setBalance(receiversBalance + amount);
        sender.sendMessage("&eYou have sent &a$" + amount + "&e to &a" + args[0] + "&e.");
        receiver.sendMessage("&eYou have received &a$" + amount + "&e from &a" + player.getName() + "&e.");
        return false;
    }
}
