package xyz.sethy.hcfactions.command.pvp;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class PvPHelpCommand extends SubCommand {
    public PvPHelpCommand() {
        super("help", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&ePvP Help &fPage");
        sender.sendMessage(" &e/pvp help &fDisplays this help page.");
        sender.sendMessage(" &e/pvp time &fCheck your current pvp time.");
        sender.sendMessage(" &e/pvp enable &fRemove your current pvp timer.");
        sender.sendMessage(" &e/lives &fCheck your current lives.");
        sender.sendMessage("&e&m-----------------------------------------------------");
    }
}
