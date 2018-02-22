package xyz.sethy.hcfactions.api.impl;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HCFProfile implements Profile {
    private final UUID uuid;
    private final Date joinedDate;
    private String name;
    private AtomicLong joinDate;
    private AtomicDouble balance;
    private AtomicInteger kills;
    private AtomicInteger deaths;
    private AtomicInteger lives;
    private AtomicLong deathbanTime;
    private AtomicLong pvpTimer;
    private AtomicBoolean joinedThisMap;
    private UUID factionId;
    private AtomicLong playTimeSinceLastDeath;
    private AtomicLong totalPlayTime;
    private transient AtomicLong joinedLast;
    private transient AtomicLong lastCached;
    private transient AtomicBoolean needsUpdate;

    public HCFProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinDate = new AtomicLong(System.currentTimeMillis());
        this.balance = new AtomicDouble(0);
        this.kills = new AtomicInteger();
        this.deaths = new AtomicInteger();
        this.lives = new AtomicInteger();
        this.deathbanTime = new AtomicLong();
        this.pvpTimer = new AtomicLong();
        this.joinedThisMap = new AtomicBoolean();
        this.joinedDate = new Date(System.currentTimeMillis());
        this.factionId = null;
        this.playTimeSinceLastDeath = new AtomicLong();
        this.totalPlayTime = new AtomicLong();
        this.joinedLast = new AtomicLong(System.currentTimeMillis());
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
        return joinDate.get();
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double balance) {
        this.balance.lazySet(balance);
        setNeedsUpdate(true);
    }

    public int getKills() {
        return kills.get();
    }

    public void setKills(int kills) {
        this.kills.lazySet(kills);
        setNeedsUpdate(true);
    }

    public int getLives() {
        return lives.get();
    }

    public void setLives(int lives) {
        this.lives.lazySet(lives);
        setNeedsUpdate(true);
    }

    public long getDeathbanTime() {
        return deathbanTime.get();
    }

    public void setDeathbanTime(long deathbanTime) {
        this.deathbanTime.lazySet(deathbanTime);
        setNeedsUpdate(true);
    }

    public long getPvpTimer() {
        return pvpTimer.get();
    }

    public void setPvpTimer(long pvpTimer) {
        this.pvpTimer.lazySet(pvpTimer);
        setNeedsUpdate(true);
    }

    public boolean hasJoinedThisMap() {
        return joinedThisMap.get();
    }

    public void setHasJoinedThisMap(boolean joinedThisMap) {
        this.joinedThisMap.lazySet(joinedThisMap);
        setNeedsUpdate(true);
    }

    public int getDeaths() {
        return deaths.get();
    }

    public void setDeaths(int deaths) {
        this.deaths.lazySet(deaths);
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
        return playTimeSinceLastDeath.get();
    }

    @Override
    public void setPlayTimeSinceLastDeath(long time) {
        this.playTimeSinceLastDeath.lazySet(time);
        setNeedsUpdate(true);
    }

    @Override
    public long addTime(long time) {
        return playTimeSinceLastDeath.addAndGet(time);
    }

    @Override
    public long getTotalPlayTime() {
        return totalPlayTime.get();
    }

    @Override
    public long getOnlineFor() {
        return System.currentTimeMillis() - joinedLast.get();
    }

    @Override
    public void setJoinedLast(long time) {
        this.joinedLast.lazySet(time);
    }

    @Override
    public long getLastCached() {
        return lastCached.get();
    }

    @Override
    public void setLastCached(long lastCached) {
        this.lastCached.lazySet(lastCached);
    }

    @Override
    public boolean needsUpdate() {
        return needsUpdate.get();
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate.set(needsUpdate);
    }
}
