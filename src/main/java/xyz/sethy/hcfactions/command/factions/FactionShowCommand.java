package xyz.sethy.hcfactions.command.factions;

import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Arrays;
import java.util.List;

public class FactionShowCommand extends SubCommand {
    public FactionShowCommand() {
        super("show", Arrays.asList("who", "info", "information"), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("&cUsage: /faction show <faction>");
            return;
        }
        if (args.length == 0) {
            Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
            if (faction == null) {
                sender.sendMessage("&cYou are not in a faction.");
                return;
            }
            sender.sendMessage(faction.getFactionInformation(sender.getUniqueId()));
            return;
        }
        List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cNo factions have been found with the name or member of '" + args[0] + "'.");
            return;
        }
        for (Faction faction : factions)
            sender.sendMessage(faction.getFactionInformation(sender.getUniqueId()));
    }
}
