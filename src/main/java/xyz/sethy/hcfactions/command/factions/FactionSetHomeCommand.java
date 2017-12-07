package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.*;
import xyz.sethy.hcfactions.api.impl.claims.HCLocation;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class FactionSetHomeCommand extends SubCommand {
    public FactionSetHomeCommand() {
        super("sethome", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction sethome");
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
        org.bukkit.Location location = Bukkit.getPlayer(sender.getUniqueId()).getLocation();
        Location location1 = new HCLocation(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());

        Map.Entry<Claim, Faction> entry = Main.getInstance().getClaimHandler().getRegionData(location);
        if (entry == null || !Objects.equal(entry.getValue().getFactionId(), faction.getFactionId())) {
            sender.sendMessage("&cYou are not currently standing inside your land.");
            return;
        }

        faction.setHome(location1);
        sender.sendMessage(
                "&eYou have set the faction's home to &f(" + location1.getX().get() + ", " + location1.getZ().get() +
                        ")&e.");
        for (UUID uuid : faction.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + sender.getName() + "&e has updated the faction's home to &f(" + location1.getX().get() + ", " + location1.getZ().get() + ")&e.");
                }
            }
        }
    }
}
