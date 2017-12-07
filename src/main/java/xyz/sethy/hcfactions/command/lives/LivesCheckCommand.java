package xyz.sethy.hcfactions.command.lives;

import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class LivesCheckCommand extends SubCommand {
    public LivesCheckCommand() {
        super("check", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("&cUsage: /lives check [player]");
            return;
        }
        if (args.length == 1) {
            Profile profile = HCFAPI.getHCFManager().findProfileByString(args[0]);
            if (profile == null) {
                sender.sendMessage("&cNo user with the name of '" + args[0] + "' was found.");
                return;
            }
            sender.sendMessage("&a" + profile.getName() + "&e's lives: " + profile.getLives());
            return;
        }
        sender.sendMessage("&a" + sender.getName() + "&e's lives: " + sender.getLives());
    }
}
