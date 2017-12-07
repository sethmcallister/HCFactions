package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;
import java.util.UUID;

public class FactionUnclaimCommand extends SubCommand {
    public FactionUnclaimCommand() {
        super("unclaim", Collections.singletonList("unclaimall"), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction unclaim");
            return;
        }
        final Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (!Objects.equal(sender.getUniqueId(), faction.getLeader().get())) {
            sender.sendMessage("&cYou must be the leader to do this.");
            return;
        }
        if (faction.isRaidable().get()) {
            sender.sendMessage("&cYou cannot un-claim faction claims while your faction is raidable.");
            return;
        }
        double refund = 0;
        refund += faction.getFactionClaim() == null ? 0 : faction.getFactionClaim().getPrice().get();
        faction.setBalance(faction.getBalance() + refund);

        Main.getInstance().getClaimHandler().setFactionAt(null, faction);
        faction.setClaim(null);

        faction.setHome(null);
        sender.sendMessage("&eYou have un-claimed your faction land, &a" + refund +
                "&e was added back into your faction's balance.");
        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage(
                            "&a" + sender.getName() + "&e has un-claimed all your faction's land.");
                    new FancyMessage("If this was an abused action, you can report it ").color(ChatColor.GOLD)
                            .then("here")
                            .color(ChatColor.WHITE)
                            .command("/report " +
                                    sender.getName() +
                                    " /faction un-claim abuse")
                            .tooltip(
                                    "Click here to report this action.")
                            .color(ChatColor.GOLD)
                            .then(".")
                            .color(ChatColor.GOLD)
                            .send(Bukkit.getPlayer(
                                    user1.getUniqueId()));
                }
            }
        }
    }
}
