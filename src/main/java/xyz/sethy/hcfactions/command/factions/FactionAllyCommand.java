package xyz.sethy.hcfactions.command.factions;

import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FactionAllyCommand extends SubCommand {
    public FactionAllyCommand() {
        super("ally", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("&cUsage: /faction ally <faction>");
            return;
        }
        final Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (!faction.isCaptainOrHigher(sender.getUniqueId())) {
            sender.sendMessage("&cYou must be at-least a captain to do this.");
            return;
        }

        List<Faction> factions = HCFAPI.getFactionManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cNo factions have been found with the name or member of '" + args[0] + "'.");
            return;
        }
        for (Faction faction1 : factions) {
            if (faction.getAllies().add(faction1.getFactionId())) {
                sender.sendMessage("&cYou are already allied with this faction.");
                return;
            }

            if (faction.getAllyRequests().contains(faction1.getFactionId())) {
                faction.getAllyRequests().remove(faction1.getFactionId());
                faction.getAllies().add(faction1.getFactionId());

                faction1.getAllies().add(faction.getFactionId());

                for (UUID uuid : faction.getAllMembers()) {
                    Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    if (user1 != null)
                        user1.sendMessage("&eYour faction is now allied with &a" + faction1.getFactionName() + "&e.");
                }
                for (UUID uuid : faction1.getAllMembers()) {
                    Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    if (user1 != null)
                        user1.sendMessage("&eYour faction is now allied with &a" + faction.getFactionName() + "&e.");
                }
                return;
            }

            faction1.getAllyRequests().add(faction.getFactionId());
            Profile leader = HCFAPI.getHCFManager().findProfileByUniqueId(faction1.getLeader().get());
            leader.sendMessage("&eThe faction &a" + faction.getFactionName().get() + "&e has requested to be your ally.");
        }
    }
}
