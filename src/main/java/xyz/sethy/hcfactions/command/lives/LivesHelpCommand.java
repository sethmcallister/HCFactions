package xyz.sethy.hcfactions.command.lives;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;

public class LivesHelpCommand extends SubCommand {
    public LivesHelpCommand() {
        super("help", new LinkedList<>(), false);
    }


    @Override
    public void execute(Profile sender, String[] args) {
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eLives Help &fPage");
        sender.sendMessage(" &e/lives &fCheck your current lives.");
        sender.sendMessage(" &e/lives check &fCheck another player's lives.");
        sender.sendMessage(" &e/lives checkdeathban &fCheck a players current deathban status.");
        sender.sendMessage(" &e/lives help &fDisplays this help page.");
        sender.sendMessage(" &e/lives send &fSend another player lives.");
        if (sender.hasPermission("hcf.staff.mod")) {
            sender.sendMessage("&eLives Staff Help");
            sender.sendMessage(" &e/lives revive &fRevive a deathbanned player.");
            sender.sendMessage(" &e/lives set &fSet a player's lives.");
        }
        sender.sendMessage("&e&m-----------------------------------------------------");
    }
}
