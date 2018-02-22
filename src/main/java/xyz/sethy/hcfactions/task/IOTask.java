package xyz.sethy.hcfactions.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.Map;

public class IOTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Map.Entry<Integer, Faction> entry : HCFAPI.getHCFManager().findAllFactions().entrySet()) {
            Faction faction = entry.getValue();
            if (faction.needsUpdate()) {
                HCFAPI.getRedisFactionDAO().update(faction);
                faction.setNeedsUpdate(false);
            }
        }

        for (Map.Entry<Integer, Profile> entry : HCFAPI.getHCFManager().findAllProfiles().entrySet()) {
            Profile profile = entry.getValue();
            if (profile.needsUpdate()) {
                HCFAPI.getRedisProfileDAO().update(profile);
            }
        }
    }
}
