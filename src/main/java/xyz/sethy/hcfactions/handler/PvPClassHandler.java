package xyz.sethy.hcfactions.handler;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClass;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClassType;
import xyz.sethy.hcfactions.pvpclass.ArcherClass;
import xyz.sethy.hcfactions.pvpclass.BardClass;
import xyz.sethy.hcfactions.pvpclass.MinerClass;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PvPClassHandler {
    private final List<PvPClass> classes;

    public PvPClassHandler() {
        this.classes = new LinkedList<>();
        this.classes.add(new BardClass());
        this.classes.add(new ArcherClass());
        this.classes.add(new MinerClass());

        for (PvPClass pvPClass : classes) {
            Bukkit.getPluginManager().registerEvents((Listener) pvPClass, Main.getInstance());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    switch (getArmorType(player)) {
                        case GOLD_INGOT: {
                            if (hasPvPClass(player)) {
                                if (!Objects.equal(getPvPClass(player), PvPClassType.BARD)) {
                                    removePlayer(player);
                                    return;
                                }
                            }
                            if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.CLASS_WARM_UP))
                                return;

                            addPlayer(PvPClassType.BARD, player);
                            break;
                        }
                        case IRON_INGOT: {
                            if (hasPvPClass(player)) {
                                if (!Objects.equal(getPvPClass(player), PvPClassType.MINER)) {
                                    removePlayer(player);
                                    return;
                                }
                            }
                            if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.CLASS_WARM_UP))
                                return;

                            addPlayer(PvPClassType.MINER, player);
                            break;
                        }
                        case LEATHER: {
                            if (hasPvPClass(player)) {
                                if (!Objects.equal(getPvPClass(player), PvPClassType.ARCHER)) {
                                    removePlayer(player);
                                    return;
                                }
                            }
                            if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.CLASS_WARM_UP))
                                return;

                            addPlayer(PvPClassType.ARCHER, player);
                            break;
                        }
                        case AIR: {
                            if (hasPvPClass(player))
                                removePlayer(player);
                        }
                        default:
                            break;
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 6L, 6L);
    }

    public boolean hasPvPClass(Player player) {
        return classes.stream().anyMatch(pClass -> pClass.getPlayers().contains(player.getUniqueId()));
    }

    public PvPClassType getPvPClass(Player player) {
        return classes.stream()
                .filter(pClass -> pClass.getPlayers().contains(player.getUniqueId()))
                .findFirst()
                .map(PvPClass::getType)
                .orElse(null);
    }

    public PvPClass getPvPClassObj(Player player) {
        return classes.stream()
                .filter(pClass -> pClass.getPlayers().contains(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    private PvPClass findByType(PvPClassType type) {
        return classes.stream().filter(pvPClass -> pvPClass.getType().equals(type)).findFirst().orElse(null);
    }

    private Material getArmorType(Player player) {
        final PlayerInventory inventory = player.getInventory();
        if (inventory.getHelmet() == null || inventory.getChestplate() == null || inventory.getLeggings() == null || inventory.getBoots() == null)
            return Material.AIR;

        if (inventory.getHelmet().getType() == Material.GOLD_HELMET &&
                inventory.getChestplate().getType() == Material.GOLD_CHESTPLATE &&
                inventory.getLeggings().getType() == Material.GOLD_LEGGINGS &&
                inventory.getBoots().getType() == Material.GOLD_BOOTS) {
            return Material.GOLD_INGOT;
        } else if (inventory.getHelmet().getType() == Material.IRON_HELMET &&
                inventory.getChestplate().getType() == Material.IRON_CHESTPLATE &&
                inventory.getLeggings().getType() == Material.IRON_LEGGINGS &&
                inventory.getBoots().getType() == Material.IRON_BOOTS) {
            return Material.IRON_INGOT;
        } else if (inventory.getHelmet().getType() == Material.LEATHER_HELMET &&
                inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
                inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS &&
                inventory.getBoots().getType() == Material.LEATHER_BOOTS) {
            return Material.LEATHER;
        } else
            return Material.AIR;
    }

    private void addPlayer(PvPClassType type, Player player) {
        final PvPClass pvPClass = findByType(type);

        if (pvPClass.getPlayers().contains(player.getUniqueId()))
            return;

        Timer timer = new DefaultTimer(TimerType.CLASS_WARM_UP, TimeUnit.SECONDS.toMillis(10L) + System.currentTimeMillis(), player);
        Main.getInstance().getTimerHandler().addTimer(player, timer);

        new BukkitRunnable() {
            @Override
            public void run() {
                pvPClass.onEquip(player);
            }
        }.runTaskLater(Main.getInstance(), 10L * 20L);
    }

    private void removePlayer(Player player) {
        PvPClassType pvPClassType = getPvPClass(player);
        if (pvPClassType == null)
            return;

        PvPClass pvPClass = findByType(pvPClassType);
        pvPClass.onUnEquip(player);
    }
}
