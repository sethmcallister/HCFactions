package xyz.sethy.hcfactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.FactionType;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.claims.HCClaim;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.impl.Koth;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        final Player player = event.getPlayer();
        final RegionData to = Main.getInstance().getClaimHandler().getRegion(event.getTo());
        final RegionData from = Main.getInstance().getClaimHandler().getRegion(event.getFrom());

        if (Objects.equals(from, to))
            return;

        String toDeathban = to.getData() == null ? FactionType.PLAYER.getDeathban() : to.getData().getFactionType().get().getDeathban();
        String fromDeathban = from.getData() == null ? FactionType.PLAYER.getDeathban() : from.getData().getFactionType().get().getDeathban();


        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNow leaving: " + from.getName(player) + " " + fromDeathban));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNow entering: " + to.getName(player) + " " + toDeathban));

        if (toDeathban.contains("Non-Deathban")) {
            Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
            if (timer != null) {
                timer.freeze();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have entered a &aNon-Deathban&e claim, your PvP Timer has been frozen."));
            }
        } else {
            Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
            if (timer != null) {
                if (timer.isFrozen()) {
                    timer.unfreeze();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have left a &aNon-Deathban&e claim, your PvP Timer has been unfrozen."));
                }
            }
        }

//        if (to.getData() != null && to.getData().getFactionType().get().equals(FactionType.KOTH)) {
//            if (!Main.getInstance().getKothHandler().isActive(to.getData()))
//                return;
//
//            Koth koth = Main.getInstance().getKothHandler().getActiveKoth(to.getData());
//            if (koth.getCurrentCapper() != null)
//                return;
//
//            koth.getTimer().unfreeze();
//            koth.getTimer().setTime(TimeUnit.MINUTES.toMillis(15L) + System.currentTimeMillis());
//
//
//            koth.setCurrentCapper(player.getUniqueId());
//            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&e Somebody has started to capture &a" + to.getData().getFactionName().get() + "&e."));
//        }
//        if (from.getData() != null && from.getData().getFactionType().get().equals(FactionType.KOTH)) {
//            if (!Main.getInstance().getKothHandler().isActive(from.getData()))
//                return;
//
//            Koth koth = Main.getInstance().getKothHandler().getActiveKoth(from.getData());
//
//            if (koth.getCurrentCapper() != null && !koth.getCurrentCapper().equals(player.getUniqueId()))
//                return;
//
//            koth.setCurrentCapper(null);
//            koth.getTimer().setTime(TimeUnit.MINUTES.toMillis(15L) + System.currentTimeMillis() + 1000L);
//            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&a " + from.getData().getFactionName().get() + "&e is no longer being captured."));
//            koth.getTimer().freeze();
//        }
    }

    @EventHandler
    public void onKothMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        boolean isToActiveKoTH = false;
        Koth toKoth = null;

        boolean isFromActiveKoTH = false;
        Koth fromKoth = null;

        for (Koth koth : Main.getInstance().getKothHandler().getActiveKoths()) {
            HCClaim claim = (HCClaim) koth.getClaim();
            if (claim.contains(to.getBlockX(), to.getBlockY(), to.getBlockZ(), to.getWorld().getUID())) {
                isToActiveKoTH = true;
                toKoth = koth;
            }

            if (claim.contains(from.getBlockX(), from.getBlockY(), from.getBlockZ(), from.getWorld().getUID())) {
                isFromActiveKoTH = true;
                fromKoth = koth;
            }
        }

        if (isToActiveKoTH && !isFromActiveKoTH) {
            if (toKoth.getCurrentCapper() != null)
                return;

            Faction faction = HCFAPI.getHCFManager().findByUniqueId(toKoth.getFactionId());

            toKoth.getTimer().unfreeze();
            toKoth.getTimer().setTime(TimeUnit.MINUTES.toMillis(15L) + System.currentTimeMillis());

            toKoth.setCurrentCapper(player.getUniqueId());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&e Somebody has started to capture &a" + faction.getFactionName().get() + "&e."));
        }

        if (!isToActiveKoTH && isFromActiveKoTH) {
            if (fromKoth.getCurrentCapper() != null && !fromKoth.getCurrentCapper().equals(player.getUniqueId()))
                return;

            Faction faction = HCFAPI.getHCFManager().findByUniqueId(fromKoth.getFactionId());

            fromKoth.setCurrentCapper(null);
            fromKoth.getTimer().setTime(TimeUnit.MINUTES.toMillis(15L) + System.currentTimeMillis() + 1000L);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aKoTH&f]&a " + faction.getFactionName().get() + "&e is no longer being captured."));
            fromKoth.getTimer().freeze();
        }
    }
}
