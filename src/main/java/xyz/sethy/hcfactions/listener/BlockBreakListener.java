package xyz.sethy.hcfactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        RegionData regionData = Main.getInstance().getClaimHandler().getRegion(location);

        if (regionData == null)
            return;

        Faction faction = regionData.getData();

        if (faction == null)
            return;

        if (!faction.getAllMembers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot do this in the territory of &c" + faction.getFactionName().get() + "&e."));

            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDiamondBreak(final BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.DIAMOND_ORE &&
                !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = 0;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    for (int z = -5; z < 5; ++z) {
                        final Block block = event.getBlock()
                                .getLocation()
                                .add((double) x, (double) y, (double) z)
                                .getBlock();
                        if (block.getType() == Material.DIAMOND_ORE && !block.hasMetadata("DiamondPlaced")) {
                            ++diamonds;
                            block.setMetadata("DiamondPlaced", new FixedMetadataValue(Main.getInstance(), true));
                        }
                    }
                }
            }

            Bukkit.getServer()
                    .broadcastMessage(
                            "[FD] " + ChatColor.AQUA + event.getPlayer().getName() + " found " + diamonds + " diamond" +
                                    ((diamonds == 1) ? "" : "s") + ".");
        }
    }
}
