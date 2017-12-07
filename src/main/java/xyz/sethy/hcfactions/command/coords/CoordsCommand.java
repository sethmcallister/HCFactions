package xyz.sethy.hcfactions.command.coords;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class CoordsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eCoords &fPage");
        sender.sendMessage(" &eSpawn &f0, 0");
        sender.sendMessage(" &eEnd Portal (Each Quadrant) &f500, 500");
        sender.sendMessage(" &eTest KoTH &f100, 100");
        sender.sendMessage("&e&m-----------------------------------------------------");
        return true;
    }
}
