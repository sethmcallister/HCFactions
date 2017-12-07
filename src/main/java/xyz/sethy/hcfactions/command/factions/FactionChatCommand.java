package xyz.sethy.hcfactions.command.factions;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.ChatMode;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.listener.AsyncChatListener;

import java.util.Collections;

public class FactionChatCommand extends SubCommand {
    public FactionChatCommand() {
        super("chat", Collections.singletonList("c"), true);
    }


    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction chat <faction|public|ally>");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "fac":
            case "f":
            case "faction": {
                AsyncChatListener.setChatMode(sender.getUniqueId(), ChatMode.FACTION);
                sender.sendMessage("&eYou are now speaking in &aFaction&e chat.");
                break;
            }
            case "a":
            case "ally": {
                AsyncChatListener.setChatMode(sender.getUniqueId(), ChatMode.ALLY);
                sender.sendMessage("&eYou are now speaking in &bAlly&e chat.");
                break;
            }
            case "p":
            case "g":
            case "global":
            case "public": {
                AsyncChatListener.setChatMode(sender.getUniqueId(), ChatMode.PUBLIC);
                sender.sendMessage("&eYou are now speaking in &fPublic&e chat.");
                break;
            }
        }
    }
}
