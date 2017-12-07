package xyz.sethy.hcfactions.command.lives;


import org.apache.commons.lang3.math.NumberUtils;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;

public class LivesSendCommand extends SubCommand {
    public LivesSendCommand() {
        super("send", Collections.singletonList("gift"), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("&cUsage: /lives send <player> <amount>");
            return;
        }
        Profile receiver = HCFAPI.getHCFManager().findProfileByString(args[0]);
        if (!NumberUtils.isNumber(args[1])) {
            sender.sendMessage("&cThe argument '" + args[1] + "' is not a number.");
            return;
        }
        Integer amount = Integer.parseInt(args[1]);
        Integer current = sender.getLives();
        if (current < amount) {
            sender.sendMessage("&cYou do not have enough lives to do this.");
            return;
        }
        receiver.setLives(receiver.getLives() + amount);
        sender.setLives(sender.getLives() - amount);
        sender.sendMessage("&eYou have sent &a" + amount + "&e lives to &a" + receiver.getName() + "&e.");
        receiver.sendMessage("&a" + sender.getName() + "&e has sent you &a" + amount + "&e lives.");
    }
}
