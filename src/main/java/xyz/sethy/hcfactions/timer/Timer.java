package xyz.sethy.hcfactions.timer;

import org.bukkit.entity.Player;

public interface Timer {
    Long getTime();

    void setTime(Long time);

    void freeze();

    void unfreeze();

    boolean isFrozen();

    TimerType getTimerType();

    Player getPlayer();

    Integer getTagLevel();
}
