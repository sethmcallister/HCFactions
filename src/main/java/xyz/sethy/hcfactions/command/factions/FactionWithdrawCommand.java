package xyz.sethy.hcfactions.command.factions;

import org.apache.commons.lang3.StringUtils;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;
import java.util.UUID;

public class FactionWithdrawCommand extends SubCommand {
    public FactionWithdrawCommand() {
        super("withdraw", Collections.singletonList("w"), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction widthdraw <#amount|all>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            double toAdd = faction.getBalance();
            sender.setBalance(sender.getBalance() + toAdd);
            faction.setBalance(0.0D);
            for (UUID uuid : faction.getAllMembers()) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + sender.getName() + "&e has withdrawn &a$" + toAdd +
                            "&e from the faction's balance.");
                }
            }
            sender.sendMessage("&eYou have withdrawn &a$" + toAdd + "&e from your faction's balance.");
            return;
        }
        if (!StringUtils.isNumeric(args[0])) {
            sender.sendMessage("&cThe argument '" + args[0] + "' is not a number.");
            return;
        }
        double amount = Double.parseDouble(args[0]);
        if (faction.getBalance() < amount) {
            sender.sendMessage("&cYour faction do not have '" + amount + "' to withdraw.");
            return;
        }
        faction.setBalance(faction.getBalance() - amount);
        sender.setBalance(sender.getBalance() + amount);

        for (UUID uuid : faction.getAllMembers()) {
            Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
            if (user1 != null) {
                user1.sendMessage("&a" + sender.getName() + "&e has withdrawn &a$" + amount +
                        "&e from the faction's balance.");
            }
        }
        sender.sendMessage("&eYou have withdrawn &a$" + amount + "&e from your faction's balance.");
    }
}
