package xyz.sethy.hcfactions.command.lives.staff;

import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class LivesReviveCommand extends SubCommand {
    public LivesReviveCommand() {
        super("revive", new LinkedList<>(), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /lives revive <player>");
            return;
        }
        if (!sender.hasPermission("hcf.staff.mod")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        Profile profile = HCFAPI.getHCFManager().findProfileByString(args[0]);
        if (profile == null) {
            sender.sendMessage("&cNo player with the name or UUID of " + args[0] + " was found.");
            return;
        }
        if ((profile.getDeathbanTime() - System.currentTimeMillis()) < 0L) {
            sender.sendMessage("&c" + profile.getName() + " is currently not death-banned.");
            return;
        }
        profile.setDeathbanTime(0L);
        sender.sendMessage("&eYou have revived &a" + profile.getName() + "&e.");
    }
}
