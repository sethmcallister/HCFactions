package xyz.sethy.hcfactions.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.impl.Koth;

import java.util.LinkedList;
import java.util.List;

public class KothHandler {
    private final List<Koth> koths;
    private final List<Koth> activeKoths;

    public KothHandler() {
        this.koths = new LinkedList<>();
        this.activeKoths = new LinkedList<>();
    }

    public void addKoth(Koth koth) {
        this.koths.add(koth);
    }

    public boolean isActive(Faction faction) {
        return this.activeKoths.stream().anyMatch(koth -> koth.getFactionId().equals(faction.getFactionId()));
    }

    public Koth getActiveKoth(Faction faction) {
        return this.activeKoths.stream()
                .filter(koth -> koth.getFactionId().equals(faction.getFactionId()))
                .findFirst()
                .orElse(null);
    }

    public Koth getKoth(Faction faction) {
        return this.koths.stream()
                .filter(koth -> koth.getFactionId().equals(faction.getFactionId()))
                .findFirst()
                .orElse(null);
    }

    public void setActive(Faction faction) {
        Koth koth = getKoth(faction);
        this.activeKoths.add(koth);
        koth.startKoth();
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&e You can now consent the KoTH &a" + faction.getFactionName().get() + "&e."));
    }

    public void setCaptured(Faction faction) {
        final Koth koth = this.getActiveKoth(faction);
        this.activeKoths.remove(koth);

        Player capper = Bukkit.getPlayer(koth.getCurrentCapper());
        Faction capperFaction = HCFAPI.getHCFManager().findByUser(capper.getUniqueId());

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&e The Faction &a" + capperFaction.getFactionName().get() + "&e has captured the KoTH &a" + faction.getFactionName().get() + "&e."));
    }

    public List<Koth> getActiveKoths() {
        return this.activeKoths;
    }

    public Koth getKoth(Claim claim) {
        return this.koths.stream().filter(koth -> koth.getClaim().equals(claim)).findFirst().orElse(null);
    }
}
