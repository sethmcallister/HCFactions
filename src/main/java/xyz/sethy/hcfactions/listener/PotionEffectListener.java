package xyz.sethy.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class PotionEffectListener implements Listener {
    private final Map<PotionEffectType, Integer> maxPotionLevels;

    public PotionEffectListener() {
        this.maxPotionLevels = new HashMap<>();
        this.maxPotionLevels.put(PotionEffectType.INCREASE_DAMAGE, 0);
    }

    @EventHandler
    public void onPotionSplash(final PotionSplashEvent event) {
        for (int i = 0; i < event.getPotion().getEffects().size(); i++) {
            PotionEffect potionEffect = (PotionEffect) event.getPotion().getEffects().toArray()[i];

            if(!this.maxPotionLevels.containsKey(potionEffect.getType()))
                continue;

            int level = maxPotionLevels.get(potionEffect.getType());
            if(potionEffect.getAmplifier() > level)
                event.getPotion().getEffects().remove(potionEffect);
        }
    }

    @EventHandler
    public void onEntityInteract(final PlayerInteractEvent event) {
        ItemStack hand = event.getPlayer().getItemInHand();
        if(hand.getType().getId() != 373)
            return;

        Potion potion = Potion.fromItemStack(hand);
        if(potion == null)
            return;

        if(!this.maxPotionLevels.containsKey(potion.getType().getEffectType())) {
            return;
        }

        int level = this.maxPotionLevels.get(potion.getType().getEffectType());
        if(potion.getLevel() > level) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to this potion.");
        }
    }
}
