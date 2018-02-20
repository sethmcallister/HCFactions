package xyz.sethy.hcfactions.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

public class IOTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Faction faction : HCFAPI.getHCFManager().findAll()) {
            if (faction.needsUpdate()) {
                HCFAPI.getRedisFactionDAO().update(faction);
                faction.setNeedsUpdate(false);
            }
        }

        for (Profile profile : HCFAPI.getHCFManager().findAllProfiles()) {
            if (profile.needsUpdate()) {
                HCFAPI.getRedisProfileDAO().update(profile);
            }
        }
    }
}
