package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.UUID;

public class FactionKickCommand extends SubCommand {
    public FactionKickCommand() {
        super("kick", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction kick <player>");
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
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("&cThe player with the name or UUID '" + args[0] + "' could not be find.");
            return;
        }
        if (!faction.getAllMembers().contains(target.getUniqueId())) {
            sender.sendMessage("&cThis player is not in your faction.");
            return;
        }
        if (faction.getCaptains().contains(sender.getUniqueId()) && faction.getCaptains().contains(target.getUniqueId())) {
            sender.sendMessage("&cYou cannot kick another captain, please ask the leader to do this.");
            return;
        }
        faction.getAllMembers().remove(target.getUniqueId());
        faction.getMembers().remove(target.getUniqueId());
        faction.getCaptains().remove(target.getUniqueId());
        Profile tp = HCFAPI.getHCFManager().findProfileByUniqueId(target.getUniqueId());
        tp.setFactionId(null);
        sender.sendMessage("&eYou have kicked &a" + target.getName() + "&e from your faction.");
        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + target.getName() + "&e has been kicked from your faction.");
                }
            }
        }
    }
}
