package xyz.sethy.hcfactions.timer;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimerHandler {
    private final Map<UUID, List<Timer>> timers;
    private boolean preSOTW;
    private boolean sotw;
    private long sotwTime;

    public TimerHandler() {
        this.timers = new ConcurrentHashMap<>();
    }

    public List<Timer> getPlayerTimers(Player player) {
        return this.timers.get(player.getUniqueId());
    }

    public boolean hasTimer(Player player, TimerType timerType) {
        if (timers.get(player.getUniqueId()) == null)
            return false;

        for (Timer timer : this.timers.get(player.getUniqueId())) {
            if (timer.getTimerType() == timerType) {
                if (timer.getTime() > 0L)
                    return true;
            }
        }
        return false;
    }

    public void addTimer(Player player, Timer defaultTimer) {
        if (timers.get(player.getUniqueId()) == null) {
            List<Timer> timersList = new LinkedList<>();
            timersList.add(defaultTimer);
            timers.put(player.getUniqueId(), timersList);
            return;
        }
        List<Timer> timersList = this.timers.get(player.getUniqueId());
        timersList.add(defaultTimer);
        timers.put(player.getUniqueId(), timersList);
    }

    public boolean hasActiveTimers(Player player) {
        if (this.timers.containsKey(player.getUniqueId()))
            return false;

        if (this.isSotw())
            return true;

        return this.timers.get(player.getUniqueId()).stream().anyMatch(timer -> timer.getTime() > 0);
    }

    public Timer getTimer(Player player, TimerType timerType) {
        if (timers.get(player.getUniqueId()) == null)
            return null;

        for (Timer timer : this.timers.get(player.getUniqueId())) {
            if (timer.getTimerType() == timerType) {
                if (timer.getTime() > 0L)
                    return timer;
            }
        }

        return null;
    }

    public Map<UUID, List<Timer>> getTimers() {
        return this.timers;
    }

    private boolean isSotw() {
        return sotw;
    }

    public void setSotw(boolean sotw) {
        this.sotw = sotw;
    }

    public long getSotwTime() {
        return sotwTime;
    }

    public void setSotwTime(long sotwTime) {
        this.sotwTime = sotwTime;
    }

    public boolean isPreSOTW() {
        return preSOTW;
    }

    public void setPreSOTW(boolean preSOTW) {
        this.preSOTW = preSOTW;
    }
}
