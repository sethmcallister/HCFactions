package xyz.sethy.hcfactions.command.lives.staff;

import org.apache.commons.lang3.math.NumberUtils;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class LivesSetCommand extends SubCommand {
    public LivesSetCommand() {
        super("set", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("&cUsage: /lives send <player> <amount>");
            return;
        }
        if (!sender.hasPermission("hcf.staff.mod")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        Profile receiver = HCFAPI.getHCFManager().findProfileByString(args[0]);
        if (!NumberUtils.isNumber(args[1])) {
            sender.sendMessage("&cThe argument '" + args[1] + "' is not a number.");
            return;
        }
        Integer amount = Integer.parseInt(args[1]);
        receiver.setLives(amount);
        sender.sendMessage("&eYou have set &a" + receiver.getName() + "&e's lives to &a" + amount + "&e.");
        receiver.sendMessage("&eYour lives have been set to &a" + amount + "&e by &a" + sender.getName() + "&e.");
    }
}
