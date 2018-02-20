package xyz.sethy.hcfactions.command.pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.goose.GooseTicker;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.Date;
import java.util.LinkedList;

public class PvPTimeCommand extends SubCommand {
    public PvPTimeCommand() {
        super("time", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        Player player = Bukkit.getPlayer(sender.getUniqueId());
        Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
        if (timer == null || timer.getTime() < 0L) {
            sender.sendMessage("&cYou do not currently have PvP Timer.");
            return;
        }
        Date time = new Date(timer.getTime() + System.currentTimeMillis());
        sender.sendMessage("&eYour PvP Timer will expire in &a" + GooseTicker.formatTime(time.getTime()) + "&e.");
    }
}
