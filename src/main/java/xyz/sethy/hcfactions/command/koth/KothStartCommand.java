package xyz.sethy.hcfactions.command.koth;

import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;

public class KothStartCommand extends SubCommand {
    public KothStartCommand() {
        super("start", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("staff.hcf.mod")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /koth start <faction>");
            return;
        }
        List<Faction> factions = HCFAPI.getHCFManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cNo factions have been found with the name or member of '" + args[0] + "'.");
            return;
        }
        Faction faction = factions.get(0);
        Main.getInstance().getKothHandler().setActive(faction);
        sender.sendMessage("&eYou have successfully started the KoTH &a" + faction.getFactionName().get() + "&e.");
    }
}
