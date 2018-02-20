package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.UUID;

public class FactionInviteCommand extends SubCommand {
    public FactionInviteCommand() {
        super("invite", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction invite <player>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (!faction.isCaptainOrHigher(sender.getUniqueId())) {
            sender.sendMessage("&cYou must be at-least a captain to do this.");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null || !player.isOnline()) {
            sender.sendMessage("&cThe player with the name or UUID '" + args[0] + "' could not be find.");
            return;
        }
        if (faction.getInvites().contains(player.getUniqueId())) {
            sender.sendMessage(
                    "&cThat player has been already invited to your faction, you can de-invite them with /f deinvite " +
                            player.getName() + ".");
            return;
        }
        faction.getInvites().add(player.getUniqueId());
        faction.setNeedsUpdate(true);
        sender.sendMessage("&eYou have invited &a" + player.getName() + "&eto your faction.");

        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + player.getName() + "&e has been invited to your faction by &a" +
                            sender.getName() + "&e.");
                }
            }
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&eYou have been invited to join the faction &a" +
                        faction.getFactionName() + "&e."));
    }
}
