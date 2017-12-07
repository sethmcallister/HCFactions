package xyz.sethy.hcfactions.command.koth;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class KothHelpCommand extends SubCommand {
    public KothHelpCommand() {
        super("help", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eKoth Help &fPage");
        sender.sendMessage(" &e/koth &fCheck the status of the current KoTH");
        if (sender.hasPermission("hcf.staff.mod")) {
            sender.sendMessage("&eLives Staff Help");
            sender.sendMessage(" &e/koth create &fCreate a new KoTH");
            sender.sendMessage(" &e/koth start &fStart a KoTH");
        }
        sender.sendMessage("&e&m-----------------------------------------------------");
    }
}
