package xyz.sethy.hcfactions.command.eotw;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class EotwStartCommand extends SubCommand {
    public EotwStartCommand() {
        super("start", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.admin")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
    }
}
