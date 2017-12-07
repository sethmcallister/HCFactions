package xyz.sethy.hcfactions.listener;

import com.google.common.base.Objects;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.LinkedList;
import java.util.List;

public class SignListener implements Listener {
    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        final Player player = event.getPlayer();
        switch (event.getLine(0).toLowerCase()) {
            case "buy": {
                Material material = Material.getMaterial(event.getLine(1));
                if (material == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(1) +
                            " is not a material."));
                    event.setCancelled(true);
                    return;
                }
                if (NumberUtils.isNumber(event.getLine(2))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(2) +
                            " is not a number."));
                    event.setCancelled(true);
                    return;
                }
                Integer amount = Integer.parseInt(event.getLine(2));
                if (NumberUtils.isDigits(event.getLine(3))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(3) +
                            " is not a double."));
                    event.setCancelled(true);
                    return;
                }
                Double cost = Double.parseDouble(event.getLine(3));

                event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&a[Purchase]"));
                event.setLine(1, material.toString());
                event.setLine(2, String.valueOf(amount));
                event.setLine(3, String.valueOf(cost));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[SUCCESS] Create a Purchase sign"));
                break;
            }
            case "sell": {
                Material material = Material.getMaterial(event.getLine(1));
                if (material == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(1) +
                            " is not a material."));
                    event.setCancelled(true);
                    return;
                }

                if (NumberUtils.isNumber(event.getLine(2))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(2) +
                            " is not a number."));
                    event.setCancelled(true);
                    return;
                }
                Integer amount = Integer.parseInt(event.getLine(2));

                if (NumberUtils.isDigits(event.getLine(3))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[ERROR] " + event.getLine(3) +
                            " is not a double."));
                    event.setCancelled(true);
                    return;
                }
                Double cost = Double.parseDouble(event.getLine(3));

                event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&a[Purchase]"));
                event.setLine(1, material.toString());
                event.setLine(2, String.valueOf(amount));
                event.setLine(3, String.valueOf(cost));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[SUCCESS] Create a Purchase sign"));
                break;
            }
            default:
                break;
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final Block block = event.getClickedBlock();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        if (block == null) {
            return;
        }

        if (!Objects.equal(block.getType(), Material.SIGN)) {
            return;
        }

        Sign sign = (Sign) block;
        switch (ChatColor.stripColor(sign.getLine(0).toLowerCase())) {
            case "[purchase]": {
                Material material = Material.getMaterial(sign.getLine(1));
                Integer amount = Integer.parseInt(sign.getLine(2));
                Double cost = Double.parseDouble(sign.getLine(3));

                Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
                if (profile.getBalance() < cost) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough money to do this."));
                    return;
                }
                ItemStack brought = new ItemStack(material, amount);
                profile.setBalance(profile.getBalance() - cost);
                player.getInventory().addItem(brought);
                player.updateInventory();
                break;
            }
            case "[sell]": {
                Material material = Material.getMaterial(sign.getLine(1));
                Integer amount = Integer.parseInt(sign.getLine(2));
                Double cost = Double.parseDouble(sign.getLine(3));

                Integer playerHas = getAmount(player, material);
                Double pricePerUnit = cost / amount;
                Double playerWillGet = pricePerUnit * playerHas;

                List<ItemStack> all = getAllFromMaterial(player, material);
                for (ItemStack itemStack : all) {
                    player.getInventory().remove(itemStack);
                }
                player.updateInventory();
                Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
                profile.setBalance(profile.getBalance() + playerWillGet);
                break;
            }
        }
    }

    private Integer getAmount(Player player, Material material) {
        int amount = 0;
        PlayerInventory itemStacks = player.getInventory();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack.getType().equals(material)) {
                amount = amount + itemStack.getAmount();
            }
        }
        return amount;
    }

    private List<ItemStack> getAllFromMaterial(Player player, Material material) {
        List<ItemStack> items = new LinkedList<>();
        PlayerInventory itemStacks = player.getInventory();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack.getType().equals(material)) {
                items.add(itemStack);
            }
        }
        return items;
    }
}
