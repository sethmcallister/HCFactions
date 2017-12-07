package xyz.sethy.hcfactions.command.sotw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class SotwEndCommand extends SubCommand {
    public SotwEndCommand() {
        super("end", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.admin")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        Main.getInstance().getTimerHandler().setSotw(false);
        Main.getInstance().getTimerHandler().setSotwTime(0L);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[SOTW]&e Start Of The World has now ended, your are no longer protected!"));
    }
}
