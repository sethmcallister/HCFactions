package xyz.sethy.hcfactions.pvpclass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClass;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClassType;

import java.util.*;

public class MinerClass extends PvPClass implements Listener {
    private final List<PotionEffect> effects;
    private final Map<UUID, List<PotionEffect>> previousEffects;


    public MinerClass() {
        super(PvPClassType.MINER);
        this.effects = new LinkedList<>();
        this.effects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));

        this.previousEffects = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : getPlayers()) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        getPlayers().remove(uuid);
                        return;
                    }
                    if (player.getLocation().getBlockY() < 20) {
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                            return;

                        PotionEffect potionEffect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
                        potionEffect.apply(player);
                        return;
                    }
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }.runTaskTimer(Main.getInstance(), 10L, 10L);
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

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Enabled: &aMiner"));
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

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Disabled: &cMiner"));
    }
}
