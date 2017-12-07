package xyz.sethy.hcfactions.command.lives;

import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Date;
import java.util.LinkedList;

public class LivesCheckDeathbanCommand extends SubCommand {
    public LivesCheckDeathbanCommand() {
        super("checkdeathban", new LinkedList<>(), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /lives checkdeathban <player>");
            return;
        }
        Profile profile = HCFAPI.getHCFManager().findProfileByString(args[0]);
        if (profile == null) {
            sender.sendMessage("&cNo player with the name or UUID of " + args[0] + " was found.");
            return;
        }
        if ((profile.getDeathbanTime() - System.currentTimeMillis()) > 0L) {
            sender.sendMessage("&c" + profile.getName() + " is currently not death-banned.");
            return;
        }
        sender.sendMessage("&a" + profile.getName() + "&e's death-ban.");
        sender.sendMessage(" &eDeath-banned Until: &a" + new Date(profile.getDeathbanTime()).toGMTString());
    }
}
