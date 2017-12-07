package xyz.sethy.hcfactions.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.HCFaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DTRHandler {
    private final double[] BASE_DTR_INCREMENT;
    private final double[] MAX_DTR;
    private Set<String> wasOnCooldown;

    public DTRHandler() {
        BASE_DTR_INCREMENT = new double[]{1.5, 0.5, 0.45, 0.4, 0.36, 0.33, 0.3, 0.27, 0.24, 0.22, 0.21, 0.2, 0.19,
                0.18, 0.175, 0.17, 0.168, 0.166, 0.164, 0.162, 0.16, 0.158, 0.156, 0.154,
                0.152, 0.15, 0.148, 0.146, 0.144, 0.142, 0.142, 0.142, 0.142, 0.142,
                0.142};
        MAX_DTR = new double[]{1.01, 1.8, 2.2, 2.7, 3.2, 3.4, 3.6, 3.8, 3.9, 4.18, 4.23, 4.36, 4.42, 4.59, 4.67, 4.72,
                4.89, 4.92, 5.04, 5.15, 5.29, 5.37, 5.48, 5.52, 5.6, 5.73, 5.81, 5.96, 6.08, 6.16,
                6.16, 6.16, 6.16, 6.16, 6.16};
        wasOnCooldown = new HashSet<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                final Map<Faction, Integer> playerOnlineMap = new ConcurrentHashMap<>();
                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                    final Faction faction = HCFAPI.getFactionManager().findByUser(player.getUniqueId());
                    if (faction != null) {
                        if (playerOnlineMap.containsKey(faction))
                            playerOnlineMap.put(faction, playerOnlineMap.get(faction) + 1);
                        else
                            playerOnlineMap.put(faction, 1);
                    }
                }
                for (final Map.Entry<Faction, Integer> factionEntry : playerOnlineMap.entrySet()) {
                    if (factionEntry.getKey().getLeader() != null) {
                        try {
                            if (isOnCooldown(factionEntry.getKey()))
                                wasOnCooldown.add(factionEntry.getKey().getFactionName().get().toLowerCase());
                            else {
                                if (wasOnCooldown.contains(factionEntry.getKey().getFactionName().get().toLowerCase())) {
                                    wasOnCooldown.remove(factionEntry.getKey().getFactionName().get().toLowerCase());
                                    for (final UUID uuid : factionEntry.getKey().getAllMembers()) {
                                        Player player = Bukkit.getPlayer(uuid);
                                        if (player == null || !player.isOnline())
                                            continue;

                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour faction is now regenerating DTR."));
                                    }
                                }
                                factionEntry.getKey()
                                        .setDTR(Math.min(factionEntry.getKey().getDTR() +
                                                        ((HCFaction) factionEntry.getKey()).getDTRIncrement(
                                                                factionEntry.getValue()).doubleValue(),
                                                factionEntry.getKey().getMaxDTR()));
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 1L, 20L);
    }


    public double getBaseDTRIncrement(final int teamsize) {
        return (teamsize == 0) ? 0.0 : (BASE_DTR_INCREMENT[teamsize - 1] * 3.0);
    }

    public double getMaxDTR(final int teamsize) {
        return (teamsize == 0) ? 100.0 : MAX_DTR[teamsize - 1];
    }

    public boolean isOnCooldown(final Faction faction) {
        return faction.getDTRFreeze().get() > System.currentTimeMillis();
    }

    public boolean isRegenerating(final Faction faction) {
        return !isOnCooldown(faction) && !Objects.equals(faction.getDTR(), faction.getMaxDTR());
    }

    public void setCooldown(final Faction faction) {
        wasOnCooldown.add(faction.getFactionName().get().toLowerCase());
    }
}
