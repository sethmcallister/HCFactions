package xyz.sethy.hcfactions.api.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.Date;
import java.util.UUID;

public class HCFProfile implements Profile {
    private final UUID uuid;
    private final Date joinedDate;
    private String name;
    private long joinDate;
    private double balance;
    private int kills;
    private int deaths;
    private int lives;
    private long deathbanTime;
    private long pvpTimer;
    private boolean joinedThisMap;
    private UUID factionId;
    private long playTimeSinceLastDeath;
    private long totalPlayTime;
    private transient long joinedLast;
    private transient long lastCached;
    private transient boolean needsUpdate;

    public HCFProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinDate = System.currentTimeMillis();
        this.balance = 0;
        this.kills = 0;
        this.deaths = 0;
        this.lives = 0;
        this.deathbanTime = 0;
        this.pvpTimer = 0;
        this.joinedThisMap = false;
        this.joinedDate = new Date(System.currentTimeMillis());
        this.factionId = null;
        this.playTimeSinceLastDeath = 0;
        this.totalPlayTime = 0;
        this.joinedLast = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBukkitColoredName() {
        return Bukkit.getPlayer(uuid).getDisplayName();
    }

    public long getJoinDate() {
        return joinDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        setNeedsUpdate(true);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        setNeedsUpdate(true);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
        setNeedsUpdate(true);
    }

    public long getDeathbanTime() {
        return deathbanTime;
    }

    public void setDeathbanTime(long deathbanTime) {
        this.deathbanTime = deathbanTime;
        setNeedsUpdate(true);
    }

    public long getPvpTimer() {
        return pvpTimer;
    }

    public void setPvpTimer(long pvpTimer) {
        this.pvpTimer = pvpTimer;
        setNeedsUpdate(true);
    }

    public boolean hasJoinedThisMap() {
        return joinedThisMap;
    }

    public void setHasJoinedThisMap(boolean joinedThisMap) {
        this.joinedThisMap = joinedThisMap;
        setNeedsUpdate(true);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        setNeedsUpdate(true);
    }

    @Override
    public void sendMessage(String s) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public boolean hasPermission(String s) {
        return Bukkit.getPlayer(uuid).hasPermission(s);
    }

    @Override
    public Date getJoinedDate() {
        return new Date(this.joinedDate.getTime());
    }

    @Override
    public UUID getFactionId() {
        return factionId;
    }

    @Override
    public void setFactionId(UUID uuid) {
        this.factionId = uuid;
    }

    @Override
    public long getPlayTimeSinceLastDeath() {
        return playTimeSinceLastDeath;
    }

    @Override
    public void setPlayTimeSinceLastDeath(long time) {
        this.playTimeSinceLastDeath = time;
    }

    @Override
    public long addTime(long time) {
        return playTimeSinceLastDeath += time;
    }

    @Override
    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    @Override
    public long getOnlineFor() {
        return System.currentTimeMillis() - joinedLast;
    }

    @Override
    public void setJoinedLast(long time) {
        this.joinedLast = time;
    }

    @Override
    public long getLastCached() {
        return lastCached;
    }

    @Override
    public void setLastCached(long lastCached) {
        this.lastCached = lastCached;
    }

    @Override
    public boolean needsUpdate() {
        return needsUpdate;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }
}
