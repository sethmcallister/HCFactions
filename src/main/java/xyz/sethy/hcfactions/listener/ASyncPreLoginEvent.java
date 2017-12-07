package xyz.sethy.hcfactions.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.CoreHCFManager;
import xyz.sethy.hcfactions.api.impl.HCFProfile;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.concurrent.TimeUnit;

public class ASyncPreLoginEvent implements Listener {
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        HCFProfile profile = (HCFProfile) HCFAPI.getHCFManager().findProfileByUniqueId(event.getUniqueId());
        if (profile == null) {
            profile = new HCFProfile(event.getUniqueId(), event.getName());
            ((CoreHCFManager) HCFAPI.getHCFManager()).addProfile(profile);
            profile.setHasJoinedThisMap(false);
            HCFAPI.getRedisProfileDAO().insert(profile);
        }
        profile.setJoinedLast(System.currentTimeMillis());
        profile.setLastCached(System.currentTimeMillis());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());

        if (!profile.hasJoinedThisMap()) {
            profile.setBalance(200);
            profile.setPvpTimer(TimeUnit.MINUTES.toMillis(30L));
            profile.setKills(0);
            profile.setDeaths(0);
            profile.setDeathbanTime(0L);

            Timer timer = new DefaultTimer(TimerType.PVP_TIMER, TimeUnit.MINUTES.toMillis(30L), player);
            Main.getInstance().getTimerHandler().addTimer(player, timer);
            profile.setHasJoinedThisMap(true);
            return;
        }
        Timer timer = new DefaultTimer(TimerType.PVP_TIMER, profile.getPvpTimer() + System.currentTimeMillis(), player);
        Main.getInstance().getTimerHandler().addTimer(player, timer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.PVP_TIMER)) {
            Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
            profile.setPvpTimer(timer.getTime() - System.currentTimeMillis());
        }
        profile.addTime(profile.getLastCached());
    }
}
