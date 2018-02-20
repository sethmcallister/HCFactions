package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FactionJoinCommand extends SubCommand {
    public FactionJoinCommand() {
        super("join", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("--force")) {
                if (!sender.hasPermission("hcf.staff.admin")) {

                    sender.sendMessage("&cYou do not have permission to execute this command.");
                    return;
                }

                List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
                if (factions.isEmpty()) {
                    sender.sendMessage("&cThere were no factions with the name or member of '" + args[0] + "' found.");
                    return;
                }
                Faction invitedto = factions.get(0);
                invitedto.getInvites().remove(sender.getUniqueId());
                invitedto.getAllMembers().add(sender.getUniqueId());
                invitedto.getMembers().add(sender.getUniqueId());
                invitedto.setNeedsUpdate(true);

                sender.sendMessage("&eYou have forcefully joined the faction &a" + invitedto.getFactionName().get() + "&e.");
                for (UUID uuid : invitedto.getAllMembers()) {
                    if (!Objects.equal(uuid, sender.getUniqueId())) {
                        Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                        if (user1 != null) {
                            user1.sendMessage("&a" + sender.getName() + "&e has forcefully joined your faction.");
                        }
                    }
                }
                sender.setFactionId(invitedto.getFactionId());
            }
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction join <faction>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction != null) {
            sender.sendMessage("&cYou are already in a faction.");
            return;
        }
        List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cThere were no factions with the name or member of '" + args[0] + "' found.");
            return;
        }
        Faction invitedto = null;
        for (Faction faction1 : factions) {
            if (faction1.getInvites().contains(sender.getUniqueId())) {
                invitedto = faction1;
                break;
            }
        }
        if (invitedto == null) {
            sender.sendMessage("&cYou have not been invited to this faction.");
            return;
        }
        invitedto.getInvites().remove(sender.getUniqueId());
        invitedto.getAllMembers().add(sender.getUniqueId());
        invitedto.getMembers().add(sender.getUniqueId());
        invitedto.setNeedsUpdate(true);

        sender.sendMessage("&eYou have joined the faction &a" + invitedto.getFactionName().get() + "&e.");
        for (UUID uuid : invitedto.getAllMembers()) {
            if (!Objects.equal(uuid, sender.getUniqueId())) {
                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                if (user1 != null) {
                    user1.sendMessage("&a" + sender.getName() + "&e has joined your faction.");
                }
            }
        }
        sender.setFactionId(invitedto.getFactionId());
    }
}
