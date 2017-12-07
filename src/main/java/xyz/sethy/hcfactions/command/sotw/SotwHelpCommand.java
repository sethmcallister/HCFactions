package xyz.sethy.hcfactions.command.sotw;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class SotwHelpCommand extends SubCommand {
    public SotwHelpCommand() {
        super("help", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.admin")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eSOTW Help &fPage");
        sender.sendMessage(" &e/sotw start &fStart the SOTW timer.");
        sender.sendMessage(" &e/sotw end &fEnd the SOTW timer.");
        sender.sendMessage("&e&m-----------------------------------------------------");
    }
}
