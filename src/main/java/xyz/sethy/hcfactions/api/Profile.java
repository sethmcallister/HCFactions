package xyz.sethy.hcfactions.api;

import java.util.Date;
import java.util.UUID;

public interface Profile {
    UUID getUniqueId();
    String getName();
    String getBukkitColoredName();
    long getJoinDate();
    double getBalance();
    void setBalance(double balance);
    int getKills();
    void setKills(int kills);
    int getLives();
    void setLives(int lives);
    long getDeathbanTime();
    void setDeathbanTime(long deathbanTime);
    long getPvpTimer();
    void setPvpTimer(long pvpTimer);
    boolean hasJoinedThisMap();
    void setHasJoinedThisMap(boolean joinedThisMap);
    int getDeaths();
    void setDeaths(int deaths);
    void sendMessage(String message);
    boolean hasPermission(String permission);
    Date getJoinedDate();
    UUID getFactionId();
    void setFactionId(UUID uuid);
    long getPlayTimeSinceLastDeath();
    long addTime(long time);
    long getTotalPlayTime();
    long getOnlineFor();
    void setJoinedLast(long time);
    long getLastCached();
    void setLastCached(long lastCached);
    void setPlayTimeSinceLastDeath(long time);
}
