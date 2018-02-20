package xyz.sethy.hcfactions.command.sotw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.util.TimerUtil;

import java.util.LinkedList;

public class SotwStartCommand extends SubCommand {
    public SotwStartCommand() {
        super("start", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.admin")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("&cUsage: /sotw start <time>");
            return;
        }

        String timeSTR = args[0];

        long time = TimerUtil.getTimeFromString(timeSTR);

        Main.getInstance().getTimerHandler().setSotw(true);
        Main.getInstance().getTimerHandler().setSotwTime(time + System.currentTimeMillis());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[SOTW]&e Start Of The World has commenced, you are now protected!"));
    }
}
