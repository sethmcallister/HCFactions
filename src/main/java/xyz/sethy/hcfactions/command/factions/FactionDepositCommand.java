package xyz.sethy.hcfactions.command.factions;

import org.apache.commons.lang3.StringUtils;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;
import java.util.UUID;

public class FactionDepositCommand extends SubCommand {
    public FactionDepositCommand() {
        super("deposit", Collections.singletonList("d"), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction deposit <#amount|all>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            double toAdd = sender.getBalance();
            sender.setBalance(0);
            faction.setBalance(faction.getBalance() + toAdd);
            for (UUID uuid : faction.getAllMembers()) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + sender.getName() + "&e has deposited &a$" + toAdd + "&e into the faction's balance.");
                }
            }
            sender.sendMessage("&eYou have deposited &a$" + toAdd + "&e into your faction's balance.");
            return;
        }
        if (!StringUtils.isNumeric(args[0])) {
            sender.sendMessage("&cThe argument '" + args[0] + "' is not a number.");
            return;
        }
        double amount = Double.parseDouble(args[0]);
        double balance = sender.getBalance();
        if (balance < amount) {
            sender.sendMessage("&cYou do not have '" + amount + "' to deposit.");
            return;
        }
        faction.setBalance(faction.getBalance() + amount);
        sender.setBalance(balance - amount);
        for (UUID uuid : faction.getAllMembers()) {
            Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
            if (user1 != null)
                user1.sendMessage("&a" + sender.getName() + "&e has deposited &a$" + amount + "&e into the faction's balance.");
        }
        sender.sendMessage("&eYou have deposited &a$" + amount + "&e into your faction's balance.");
    }
}
