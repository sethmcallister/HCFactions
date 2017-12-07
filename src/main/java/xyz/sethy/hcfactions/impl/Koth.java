package xyz.sethy.hcfactions.impl;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Koth {
    private final UUID factionId;
    private Timer timer;
    private Claim claim;
    private UUID currentCapper;

    public Koth(UUID factionId, Claim claim) {
        this.factionId = factionId;
        this.claim = claim;
    }

    public void startKoth() {
        this.timer = new DefaultTimer(TimerType.KOTH, TimeUnit.MINUTES.toMillis(15L) + System.currentTimeMillis(), null);
        new BukkitRunnable() {
            @Override
            public void run() {
                timer.freeze();
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), 2L);
    }

    public UUID getFactionId() {
        return factionId;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public UUID getCurrentCapper() {
        return currentCapper;
    }

    public void setCurrentCapper(UUID currentCapper) {
        this.currentCapper = currentCapper;
    }

    public Timer getTimer() {
        return timer;
    }
}
