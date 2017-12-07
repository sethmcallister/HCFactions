package xyz.sethy.hcfactions.command.pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.LinkedList;

public class PvPEnableCommand extends SubCommand {
    public PvPEnableCommand() {
        super("enable", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        Player player = Bukkit.getPlayer(sender.getUniqueId());
        Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
        if (timer == null || timer.getTime() < 0L) {
            sender.sendMessage("&cYou do not currently have PvP Timer.");
            return;
        }
        sender.setPvpTimer(0L);
        timer.setTime(0L);
        sender.sendMessage("&aYou have removed your PvP Timer.");
    }
}
