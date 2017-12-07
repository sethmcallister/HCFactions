package xyz.sethy.hcfactions.handler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemHandler {
    private ItemStack claimWand;

    public ItemHandler() {
        claimWand = new ItemStack(Material.GOLD_HOE);
        ItemMeta itemMeta = claimWand.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eClaim Wand"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eLeft Click: &aSelection location 1"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eRight Click: &aSelection location 2"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eRight Click & Shift: &aClaim Land"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eLeft Click & Shift: &aClear Claim Selection"));
        itemMeta.setLore(lore);
        claimWand.setItemMeta(itemMeta);
    }

    public ItemStack getClaimWand() {
        return claimWand;
    }
}
