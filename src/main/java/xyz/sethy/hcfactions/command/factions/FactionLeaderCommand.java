package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;
import java.util.UUID;

public class FactionLeaderCommand extends SubCommand {
    public FactionLeaderCommand() {
        super("leader", Collections.singletonList("admin"), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("--force")) {
                if (!sender.hasPermission("hcf.staff.admin")) {
                    sender.sendMessage("&cYou do not have permission to execute this command.");
                    return;
                }
                Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
                if (faction == null) {
                    sender.sendMessage("&cYou are not in a faction.");
                    return;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage("&cThe player with the name or UUID '" + args[0] + "' could not be find.");
                    return;
                }
                if (!faction.getAllMembers().contains(target.getUniqueId())) {
                    sender.sendMessage("&cThis player is not in your faction.");
                    return;
                }
                faction.getLeader().set(target.getUniqueId());
                sender.sendMessage(
                        "&eYou have forcefully promoted &a" + target.getName() + "&e to the faction's leader.");
                target.sendMessage("&eYou have been forcefully promoted to the faction's leader.");
                for (UUID uuid : faction.getAllMembers()) {
                    if (!Objects.equal(uuid, sender.getUniqueId()) || !Objects.equal(uuid, target.getUniqueId())) {
                        Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                        if (user1 != null) {
                            user1.sendMessage("&a" + target.getName() +
                                    "&e has been forcefully promoted to the factions leader.");
                        }
                    }
                }
            }
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction leader <player>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (!Objects.equal(sender.getUniqueId(), faction.getLeader().get())) {
            sender.sendMessage("&cYou must be the leader to do this.");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("&cThe player with the name or UUID '" + args[0] + "' could not be find.");
            return;
        }
        if (!faction.getAllMembers().contains(target.getUniqueId())) {
            sender.sendMessage("&cThis player is not in your faction.");
            return;
        }
        faction.getLeader().set(target.getUniqueId());
        sender.sendMessage("&eYou have promoted &a" + target.getName() + "&e to the faction's leader.");
        target.sendMessage("&eYou have been promoted to the faction's leader.");
        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId()) || !Objects.equal(uuid, target.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + target.getName() + "&e has been promoted to the factions leader.");
                }
            }
        }
    }
}
