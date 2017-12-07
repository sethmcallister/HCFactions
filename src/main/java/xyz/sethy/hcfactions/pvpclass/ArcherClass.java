package xyz.sethy.hcfactions.pvpclass;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClass;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClassType;
import xyz.sethy.hcfactions.timer.ArcherTag;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ArcherClass extends PvPClass implements Listener {
    private final List<PotionEffect> effects;
    private final Map<UUID, Long> effectCooldowns;
    private final Map<UUID, List<PotionEffect>> previousEffects;

    public ArcherClass() {
        super(PvPClassType.ARCHER);

        this.effects = new ArrayList<>();
        this.effects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));

        this.previousEffects = new HashMap<>();

        this.effectCooldowns = new HashMap<>();
    }

    @Override
    public void onEquip(Player player) {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.addAll(player.getActivePotionEffects());
        player.getActivePotionEffects().clear();

        this.previousEffects.put(player.getUniqueId(), potionEffects);
        for (PotionEffect effect : this.effects) {
            if (player.hasPotionEffect(effect.getType())) {
                player.removePotionEffect(effect.getType());
            }
            effect.apply(player);
        }

        getPlayers().add(player.getUniqueId());

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Enabled: &aArcher"));
    }

    @Override
    public void onUnEquip(Player player) {
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        for (PotionEffect type : this.effects) {
            player.removePotionEffect(type.getType());
        }

        for (PotionEffect potionEffect : this.previousEffects.get(player.getUniqueId())) {
            potionEffect.apply(player);
        }

        getPlayers().remove(player.getUniqueId());

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Disabled: &cArcher"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!getPlayers().contains(event.getPlayer().getUniqueId()))
            return;

        final Player player = event.getPlayer();

        final ItemStack hand = player.getItemInHand();
        if (hand == null)
            return;

        if (this.effectCooldowns.containsKey(player.getUniqueId()) && this.effectCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = this.effectCooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            double value = millisLeft / 1000.0D;
            double sec = Math.round(10.0D * value) / 10.0D;
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this for another &l" + sec + "&c seconds."));
            return;
        }

        switch (hand.getType()) {
            case SUGAR: {
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 5 * 20, 3);
                potionEffect.apply(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have given yourself &aSpeed 4&e for &a5 seconds&e."));
                this.effectCooldowns.put(player.getUniqueId(), 16000L + System.currentTimeMillis());
                Timer timer = new DefaultTimer(TimerType.ARCHER_COOLDOWN, 16000L + System.currentTimeMillis(), player);
                Main.getInstance().getTimerHandler().addTimer(player, timer);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PotionEffect potionEffect1 = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
                        potionEffect1.apply(player);
                    }
                }.runTaskLater(Main.getInstance(), 5L * 20L);
                break;
            }
            default:
                break;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Arrow))
            return;

        final Arrow arrow = (Arrow) event.getDamager();

        if ((arrow.getShooter() instanceof Player))
            return;

        final Player player = (Player) arrow.getShooter();

        if (!getPlayers().contains(player.getUniqueId()))
            return;

        final Player hit = (Player) event.getEntity();

        if (Main.getInstance().getTimerHandler().hasTimer(hit, TimerType.ARCHER_TAG)) {
            ArcherTag timer = (ArcherTag) Main.getInstance()
                    .getTimerHandler()
                    .getTimer(hit, TimerType.ARCHER_TAG);
            switch (timer.getTagLevel()) {
                case 1: {
                    timer.setTagLevel(2);
                    break;
                }
                case 2: {
                    timer.setTagLevel(3);
                    break;
                }
                default:
                    break;
            }
            timer.setTime(TimeUnit.MINUTES.toMillis(30L) + System.currentTimeMillis());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&eYou have Archer Tagged &a" + hit.getName() +
                            "&e at level &a" + timer.getTagLevel() +
                            "&e for &a30 seconds&e."));
            hit.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cMarked!&e You have been Archer Tagged at level &a" +
                            timer.getTagLevel() + "&e for &a30 seconds&e."));
            return;
        }

        final Timer timer = new DefaultTimer(TimerType.ARCHER_TAG,
                TimeUnit.MINUTES.toMillis(30L) + System.currentTimeMillis(), hit);
        Main.getInstance().getTimerHandler().addTimer(hit, timer);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have Archer Tagged &a" + hit.getName() +
                "&e at level &a" + timer.getTagLevel() +
                "&e for &a30 seconds&e."));
        hit.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&cMarked!&e You have been Archer Tagged at level &a" +
                        timer.getTagLevel() + "&e for &a30 seconds&e."));
    }
}
