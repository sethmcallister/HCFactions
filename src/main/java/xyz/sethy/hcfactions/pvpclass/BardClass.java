package xyz.sethy.hcfactions.pvpclass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.FactionType;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClass;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClassType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BardClass extends PvPClass implements Listener {
    private final List<PotionEffect> effects;
    private final Map<UUID, List<PotionEffect>> previousEffects;
    private final Map<UUID, List<PotionEffect>> cachedEffects;
    private final Map<UUID, Long> effectCooldowns;
    private final Map<UUID, Integer> energy;

    public BardClass() {
        super(PvPClassType.BARD);
        this.effects = new ArrayList<>();
        this.effects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.effects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.energy = new HashMap<>();

        this.previousEffects = new ConcurrentHashMap<>();
        this.cachedEffects = new ConcurrentHashMap<>();
        this.effectCooldowns = new ConcurrentHashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        continue;

                    Faction faction = HCFAPI.getFactionManager().findByUser(player.getUniqueId());
                    if (faction == null)
                        continue;

                    RegionData regionData = Main.getInstance().getClaimHandler().getRegion(player.getLocation());
                    if (regionData != null && regionData.getData() != null && regionData.getData().getFactionType().get().equals(FactionType.SAFE))
                        return;

                    if (player.getItemInHand() == null)
                        continue;

                    switch (player.getItemInHand().getType()) {
                        case BLAZE_ROD: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    return;

                                if (member.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                                    member.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                                member.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0));
                            }
                            break;
                        }
                        case IRON_INGOT: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    continue;

                                if (member.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                    member.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                }

                                member.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0));
                            }
                            break;
                        }
                        case SUGAR: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    continue;

                                if (member.hasPotionEffect(PotionEffectType.SPEED)) {
//                                    for(PotionEffect potionEffect : member.getActivePotionEffects())
//                                    {
//                                        if(potionEffect.getType().equals(PotionEffectType.SPEED) && potionEffect.getAmplifier() <= 1)
//                                        {
//                                            List<PotionEffect> effects = cachedEffects.get(player.getUniqueId());
//                                            if(effects == null)
//                                                effects = new LinkedList<>();
//
//                                            effects.add(potionEffect);
//                                            cachedEffects.put(player.getUniqueId(), effects);
//                                        }
//                                    }

                                    member.removePotionEffect(PotionEffectType.SPEED);
                                }

                                member.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                            }
                            break;
                        }
                        case GHAST_TEAR: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    continue;

                                if (member.hasPotionEffect(PotionEffectType.REGENERATION))
                                    member.removePotionEffect(PotionEffectType.REGENERATION);

                                member.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                            }
                            break;
                        }
                        case FEATHER: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    continue;

                                if (member.hasPotionEffect(PotionEffectType.JUMP))
                                    member.removePotionEffect(PotionEffectType.JUMP);

                                member.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 0));
                            }
                            break;
                        }
                        case MAGMA_CREAM: {
                            for (UUID memberUUID : faction.getAllMembers()) {
                                if (memberUUID.equals(player.getUniqueId()))
                                    continue;

                                Player member = Bukkit.getPlayer(memberUUID);
                                if (member == null)
                                    continue;

                                if (player.getLocation().distance(member.getLocation()) >= 20)
                                    continue;

                                if (member.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
                                    member.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);

                                member.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20, 0));
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 3L, 3L);
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
        this.energy.put(player.getUniqueId(), 0);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Enabled: &aBard"));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getPlayers().contains(player.getUniqueId())) {
                    cancel();
                    return;
                }
                Integer energy = getEnergy().get(player.getUniqueId());
                if (energy >= 100)
                    return;

                getEnergy().put(player.getUniqueId(), energy + 1);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
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

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eClass Disabled: &cBard"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!getPlayers().contains(event.getPlayer().getUniqueId()))
            return;

        Player player = event.getPlayer();

        ItemStack hand = player.getItemInHand();
        if (hand == null)
            return;

        RegionData regionData = Main.getInstance().getClaimHandler().getRegion(player.getLocation());
        if (regionData != null && regionData.getData() != null && regionData.getData().getFactionType().get().equals(FactionType.SAFE))
            return;

        Faction faction = HCFAPI.getFactionManager().findByUser(player.getUniqueId());
        if (faction == null)
            return;

        if (this.effectCooldowns.containsKey(player.getUniqueId()) && this.effectCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = this.effectCooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            double value = millisLeft / 1000.0D;
            double sec = Math.round(10.0D * value) / 10.0D;
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this for another &l" + sec + "&c seconds."));
            return;
        }

        switch (hand.getType()) {
            case FEATHER: {
                int energy = this.energy.get(player.getUniqueId());
                if (energy < 10) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough energy to do this, you need 10."));
                    return;
                }

                List<Player> affected = new ArrayList<>();
                for (UUID memberUUID : faction.getAllMembers()) {
                    if (memberUUID.equals(player.getUniqueId()))
                        continue;

                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member == null)
                        continue;

                    if (player.getLocation().distance(member.getLocation()) >= 20)
                        continue;

                    if (member.hasPotionEffect(PotionEffectType.JUMP))
                        member.removePotionEffect(PotionEffectType.JUMP);

                    member.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 3));
                    affected.add(member);

                    member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been given &aJump Boost 4&e for &a5 seconds&e."));
                }

                decrementHand(player);

                this.energy.put(player.getUniqueId(), energy - 10);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have given &a" + Arrays.toString(affected.toArray()) + " Jump Boost 4&e for &a5 seconds&e."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou now have &a" + this.energy.get(player.getUniqueId()) + "&e(-10)."));
                break;
            }
            default:
                break;
        }
    }

    private void decrementHand(Player player) {
        ItemStack hand = player.getItemInHand();
        if (hand.getAmount() == 1) {
            player.getInventory().remove(hand);
        }
        hand.setAmount(hand.getAmount() - 1);
    }

    public Map<UUID, Integer> getEnergy() {
        return energy;
    }
}
