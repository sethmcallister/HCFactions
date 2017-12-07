package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.UUID;

public class FactionDisbandCommand extends SubCommand {
    public FactionDisbandCommand() {
        super("disband", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage("&cUsage: /faction disband");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (!Objects.equal(faction.getLeader().get(), sender.getUniqueId())) {
            sender.sendMessage("&cYou must be the leader of this faction to disband it.");
            return;
        }
        int toRefund = 0;
        if (faction.getFactionClaim() != null) {
            toRefund += faction.getFactionClaim().getPrice().get();
        }
        toRefund += faction.getBalance();
        sender.setBalance(sender.getBalance() + toRefund);

        (HCFAPI.getFactionManager()).disbandFaction(faction);

        for (UUID uuid : faction.getAllMembers()) {
            Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
            if (user1 != null)
                user1.sendMessage("&eThe faction you where in has been disbanded.");
        }
        sender.sendMessage("&aYou have successfully disbanded your faction.");
        sender.setFactionId(null);
    }
}
