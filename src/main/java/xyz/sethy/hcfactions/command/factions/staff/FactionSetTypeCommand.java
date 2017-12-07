package xyz.sethy.hcfactions.command.factions.staff;

import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.FactionType;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;

public class FactionSetTypeCommand extends SubCommand {
    public FactionSetTypeCommand() {
        super("settype", new LinkedList<>(), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.developer")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        if (args.length != 2) {
            sender.sendMessage("&cUsage: /faction settype <faction> <factionType>");
            return;
        }
        List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cThere were no factions with the name or member of '" + args[0] + "' found.");
            return;
        }
        Faction first = factions.get(0);
        FactionType factionType = FactionType.PLAYER;
        for (FactionType type1 : FactionType.values()) {
            if (args[1].toLowerCase().equals(type1.toString().toLowerCase())) {
                factionType = type1;
            }
        }
        first.getFactionType().set(factionType);
        HCFAPI.getRedisFactionDAO().update(first);
        sender.sendMessage("&eYou have updated &a" + first.getFactionName() + "&e to &a" + factionType + "&e.");
    }
}
