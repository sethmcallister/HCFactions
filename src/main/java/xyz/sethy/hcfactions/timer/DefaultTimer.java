package xyz.sethy.hcfactions.timer;

import org.bukkit.entity.Player;

public class DefaultTimer implements Timer {
    private TimerType timerType;
    private Long time;
    private Player player;
    private boolean frozen;
    private Integer tag = 1;
    private Long frozenAt;

    public DefaultTimer(TimerType timerType, Long time, Player player) {
        this.timerType = timerType;
        this.time = time;
        this.player = player;
        frozen = false;
    }

    @Override
    public Long getTime() {
        if (frozen)
            return time - frozenAt;

        return time - System.currentTimeMillis();
    }

    @Override
    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public void freeze() {
        this.frozen = true;
        this.frozenAt = System.currentTimeMillis();
    }

    @Override
    public void unfreeze() {
        this.frozen = false;
        long add = System.currentTimeMillis() - frozenAt;
        this.time = time + add;
        this.frozenAt = 0L;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public TimerType getTimerType() {
        return timerType;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Integer getTagLevel() {
        return tag;
    }

    @Override
    public String toString() {
        return this.time.toString() + ":" + this.player.getUniqueId().toString() + ":" + this.frozen + ":" +
                this.timerType.toString();
    }
}
