package xyz.sethy.hcfactions.api.impl;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.HCFManager;
import xyz.sethy.hcfactions.api.Profile;

import javax.xml.ws.FaultAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CoreHCFManager implements HCFManager {
    private final Map<Integer, Faction> factions;
    private final Map<Integer, Profile> profiles;

    public CoreHCFManager() {
        this.factions = new ConcurrentHashMap<>();
        this.profiles = new ConcurrentHashMap<>();
        int i = 0;
        for (Faction faction : HCFAPI.getRedisFactionDAO().findAll()) {
            this.factions.put(i, faction);
            i++;
        }
        i = 0;
        for (Profile profile : HCFAPI.getRedisProfileDAO().findAll()) {
            profiles.put(i, profile);
            i++;
        }
    }

    @Override
    public Faction findByUniqueId(final UUID uuid) {
        return factions.entrySet().parallelStream().filter(entry -> entry.getValue().getFactionId().equals(uuid)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public Faction findByUser(final UUID user) {
        return factions.entrySet().parallelStream().filter(entry -> entry.getValue().getAllMembers().stream().anyMatch(uuid -> Objects.equal(user, uuid))).findFirst().map(Map.Entry::getValue).orElse(null);

    }

    @Override
    public List<Faction> findByString(final String s) {
        List<Faction> factionList = new ArrayList<>();
        factions.forEach((key, value) -> {
            if (value.getFactionName().get().equalsIgnoreCase(s)) {
                factionList.add(value);
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(s);
            if (player == null)
                return;
            if (value.getAllMembers().contains(player.getUniqueId()))
                factionList.add(value);
        });
        return factionList;
    }

    @Override
    public Map<Integer, Faction> findAllFactions() {
        return factions;
    }

    public void disbandFaction(Faction faction) {
        factions.forEach((key, value) -> {
            if (value.getFactionId().equals(faction.getFactionId()))
                this.factions.remove(key);
        });
        HCFAPI.getRedisFactionDAO().delete(faction);
    }

    @Override
    public Profile findProfileByUniqueId(UUID uuid) {
        return this.profiles.entrySet().stream().filter(entry -> entry.getValue().getUniqueId().equals(uuid)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public Profile findProfileByString(String s) {
        return this.profiles.entrySet().stream().filter(entry -> entry.getValue().getName().equalsIgnoreCase(s)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public Map<Integer, Profile> findAllProfiles() {
        return profiles;
    }

    public void addFaction(Faction faction) {
        this.factions.put(this.factions.size(), faction);
    }

    public void addProfile(Profile profile) {
        this.profiles.put(this.profiles.size(), profile);
    }
}
