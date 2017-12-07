package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Arrays;
import java.util.UUID;

public class FactionPromoteCommand extends SubCommand {
    public FactionPromoteCommand() {
        super("promote", Arrays.asList("captain", "moderator", "mod"), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction promote <player>");
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
        if (faction.getCaptains().contains(target.getUniqueId())) {
            sender.sendMessage("&cThis player is already a faction captain.");
            return;
        }
        if (faction.getLeader().get().equals(target.getUniqueId())) {
            sender.sendMessage("&cThis player is already the faction leader.");
            return;
        }
        if (faction.getCoLeaders().contains(target.getUniqueId())) {
            sender.sendMessage("&cThis player is already a faction coleader.");
            return;
        }
        faction.getCaptains().add(target.getUniqueId());
        sender.sendMessage("&eYou have promoted &a" + target.getName() + "&e to a faction captain.");
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been promoted to a faction captain."));
        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId()) || !Objects.equal(uuid, target.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null)
                    user1.sendMessage("&a" + target.getName() + "&e has been promoted to a faction captain.");
            }
        }
    }
}