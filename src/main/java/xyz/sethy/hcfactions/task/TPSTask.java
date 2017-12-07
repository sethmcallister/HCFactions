package xyz.sethy.hcfactions.task;

import org.bukkit.scheduler.BukkitRunnable;

public class TPSTask extends BukkitRunnable {
    private long sec;
    private long currentSec;
    private double tps;
    private int ticks;

    public TPSTask() {
        sec = 0;
        currentSec = 0;
        tps = 20.0;
        ticks = 0;
    }

    public void run() {
        sec = (System.currentTimeMillis() / 1000);

        if (currentSec == sec)
            ticks++;
        else
            currentSec = sec;
        tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
        ticks = 0;
    }

    public double getTps() {
        return tps;
    }
}
