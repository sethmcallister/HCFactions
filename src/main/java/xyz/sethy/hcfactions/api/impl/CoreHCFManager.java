package xyz.sethy.hcfactions.api.impl;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.HCFManager;
import xyz.sethy.hcfactions.api.Profile;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CoreHCFManager implements HCFManager {
    private final List<Faction> factions;
    private final List<Profile> profiles;

    public CoreHCFManager() {
        this.factions = new LinkedList<>();
        this.profiles = new LinkedList<>();
        this.factions.addAll(HCFAPI.getRedisFactionDAO().findAll());
        this.profiles.addAll(HCFAPI.getRedisProfileDAO().findAll());
    }

    @Override
    public Faction findByUniqueId(final UUID uuid) {
        return factions.stream().filter(faction -> Objects.equal(uuid, faction.getFactionId())).findFirst().orElse(null);
    }

    @Override
    public Faction findByUser(final UUID user) {
        for (Faction faction : factions)
            for (UUID uuid : faction.getAllMembers())
                if (Objects.equal(user, uuid))
                    return faction;

        return null;
    }

    @Override
    public List<Faction> findByString(final String s) {
        List<Faction> factionList = factions.stream().filter(faction -> Objects.equal(faction.getFactionName().get().toLowerCase(), s.toLowerCase())).collect(Collectors.toCollection(LinkedList::new));

        for (Faction faction : factionList) {
            for (UUID uuid : faction.getAllMembers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (Objects.equal(offlinePlayer.getName().toLowerCase(), s.toLowerCase()))
                        factionList.add(faction);
                    continue;
                }
                if (Objects.equal(player.getName().toLowerCase(), s.toLowerCase()))
                    factionList.add(faction);
            }
        }
        return factionList;
    }

    @Override
    public List<Faction> findAll() {
        return factions;
    }

    public void disbandFaction(Faction faction) {
        this.factions.remove(faction);
        HCFAPI.getRedisFactionDAO().delete(faction);
    }

    @Override
    public Profile findProfileByUniqueId(UUID uuid) {
        return this.profiles.stream().filter(profile -> profile.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public Profile findProfileByString(String s) {
        return this.profiles.stream().filter(profile -> profile.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    @Override
    public List<Profile> findAllProfiles() {
        return profiles;
    }

    public void addFaction(Faction faction) {
        if (!this.factions.contains(faction)) {
            this.factions.add(faction);
            HCFAPI.getRedisFactionDAO().insert(faction);
        }
    }

    public void addProfile(Profile profile) {
        if (!this.profiles.contains(profile)) {
            this.profiles.add(profile);
        }
    }
}
