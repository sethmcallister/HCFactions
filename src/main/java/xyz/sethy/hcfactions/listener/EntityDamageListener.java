package xyz.sethy.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.UUID;

public class EntityDamageListener implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) && !(event.getEntity() instanceof Player))
            return;

        final Player damaged = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();
        final Faction faction = HCFAPI.getFactionManager().findByUser(damager.getUniqueId());

        if (faction != null) {
            if (faction.getAllMembers().contains(damaged.getUniqueId())) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWatch out! &f" + damaged.getName() +
                        "&a is in your faction."));
                return;
            }
            for (UUID allyUUID : faction.getAllies()) {
                Faction ally = HCFAPI.getFactionManager().findByUniqueId(allyUUID);
                if (ally.getAllMembers().contains(damaged.getUniqueId())) {
                    damager.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bWatch out! &f" + damaged.getName() +
                                    "&b is an ally."));
                }
            }
        }

        if (Main.getInstance().getTimerHandler().hasTimer(damager, TimerType.PVP_TIMER)) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou still have PvP Timer."));
            return;
        }

        if (Main.getInstance().getTimerHandler().hasTimer(damaged, TimerType.PVP_TIMER)) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThat player still has their PvP Timer."));
            return;
        }

        if (Main.getInstance().getTimerHandler().hasTimer(damaged, TimerType.COMBAT_TAG)) {
            Timer timer = Main.getInstance().getTimerHandler().getTimer(damaged, TimerType.COMBAT_TAG);
            timer.setTime(30000 + System.currentTimeMillis());
        } else {
            Timer timer = new DefaultTimer(TimerType.COMBAT_TAG, 30000 + System.currentTimeMillis(), damaged);
            Main.getInstance().getTimerHandler().addTimer(damaged, timer);
            damaged.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&eYou have been spawn-tagged for &c30&e seconds."));
        }

        if (Main.getInstance().getTimerHandler().hasTimer(damager, TimerType.COMBAT_TAG)) {
            Timer timer = Main.getInstance().getTimerHandler().getTimer(damager, TimerType.COMBAT_TAG);
            timer.setTime(30000 + System.currentTimeMillis());
        } else {
            Timer timer = new DefaultTimer(TimerType.COMBAT_TAG, 30000 + System.currentTimeMillis(), damager);
            Main.getInstance().getTimerHandler().addTimer(damager, timer);
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&eYou have been spawn-tagged for &c30&e seconds."));
        }
    }
}
