package xyz.sethy.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.packet.AnvilSoundPacket;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        RegionData regionData = Main.getInstance().getClaimHandler().getRegion(location);

        if (regionData == null)
            return;

        Faction faction = regionData.getData();

        if (faction == null)
            return;

        if (faction.getAllMembers().contains(player.getUniqueId()))
            return;

        if (event.getItemInHand().getType().equals(Material.ANVIL)) {
            AnvilSoundPacket.addLocation(location);
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot do this in the territory of &c" + faction.getFactionName().get() + "&e."));
    }
}
