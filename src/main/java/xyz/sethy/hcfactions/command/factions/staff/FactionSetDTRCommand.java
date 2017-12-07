package xyz.sethy.hcfactions.command.factions.staff;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;

public class FactionSetDTRCommand extends SubCommand {
    public FactionSetDTRCommand() {
        super("setdtr", new LinkedList<>(), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("hcf.staff.mod")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        if (args.length != 2) {
            sender.sendMessage("&cUsage: /faction setdtr <faction> <dtr>");
            return;
        }
        List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cThere were no factions with the name or member of '" + args[0] + "' found.");
            return;
        }
        Faction first = factions.get(0);
        if (!NumberUtils.isNumber(args[1])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe argument '" + args[1] + "' is not a number."));
            return;
        }
        Double newDtr = Double.valueOf(args[1]);

        if (newDtr > first.getMaxDTR())
            newDtr = first.getMaxDTR();

        first.setDTR(newDtr);
        first.getDTRFreeze().set(0L);
        sender.sendMessage("&eYou have updated &a" + first.getFactionName() + "&e's DTR to &a" + newDtr + "&e.");
    }
}
