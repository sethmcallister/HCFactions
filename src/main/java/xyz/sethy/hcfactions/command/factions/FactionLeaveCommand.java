package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.UUID;

public class FactionLeaveCommand extends SubCommand {
    public FactionLeaveCommand() {
        super("leave", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("--force")) {
                if (!sender.hasPermission("hcf.staff.admin")) {
                    sender.sendMessage("&cYou do not have permission to execute this command.");
                    return;
                }
                Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
                if (faction == null) {
                    sender.sendMessage("&cYou are not in a faction.");
                    return;
                }
                UUID currentLeader = faction.getFactionId();
                faction.getCaptains().add(currentLeader);

                faction.getAllMembers().remove(sender.getUniqueId());
                faction.getMembers().remove(sender.getUniqueId());
                faction.getCaptains().remove(sender.getUniqueId());
                faction.setNeedsUpdate(true);
                sender.sendMessage("&eYou have forcefully left the faction &a" + faction.getFactionName() + "&e.");
                for (UUID uuid : faction.getAllMembers()) {
                    Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    if (user1 != null)
                        user1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + sender.getName() + "&e has forcefully left the faction."));
                }
                sender.setFactionId(null);
            }
            return;
        }
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction leave");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (Objects.equal(faction.getLeader().get(), sender.getUniqueId())) {
            sender.sendMessage("&cYou cannot leave this faction as you are the leader, either disband or appoint a new leader.");
            return;
        }
        UUID currentLeader = faction.getFactionId();
        faction.getAllMembers().remove(sender.getUniqueId());
        faction.getMembers().remove(sender.getUniqueId());
        faction.getCaptains().remove(sender.getUniqueId());
        faction.getCaptains().add(currentLeader);
        faction.setNeedsUpdate(true);
        sender.sendMessage("&eYou have left the faction &a" + faction.getFactionName() + "&e.");
        for (UUID uuid : faction.getAllMembers()) {
            Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
            if (user1 != null) {
                user1.sendMessage("&a" + sender.getName() + "&e has left the faction.");
            }
        }
        sender.setFactionId(null);
    }
}
