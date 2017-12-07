package xyz.sethy.hcfactions.handler;


import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.concurrent.TimeUnit;

public class DeathbanHandler {
    public long getDeathbanTime(Player player) {
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        profile.addTime(System.currentTimeMillis() - profile.getLastCached());
        profile.setLastCached(System.currentTimeMillis());

        long sixHours = TimeUnit.HOURS.toMillis(6L);
        if(profile.getPlayTimeSinceLastDeath() >= sixHours) {
            return sixHours;
        }
        return profile.getPlayTimeSinceLastDeath();
    }
}
