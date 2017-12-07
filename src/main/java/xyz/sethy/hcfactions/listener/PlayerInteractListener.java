package xyz.sethy.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.LinkedList;
import java.util.List;

public class PlayerInteractListener implements Listener {
    private final List<Material> blockedInteractions;

    public PlayerInteractListener() {
        this.blockedInteractions = new LinkedList<>();
        this.blockedInteractions.add(Material.FENCE_GATE);
        this.blockedInteractions.add(Material.WOODEN_DOOR);
        this.blockedInteractions.add(Material.LEVER);
        this.blockedInteractions.add(Material.STONE_BUTTON);
        this.blockedInteractions.add(Material.WOOD_BUTTON);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null)
            return;

        RegionData regionData = Main.getInstance().getClaimHandler().getRegion(block.getLocation());

        if (regionData.getData() == null)
            return;

        Faction faction = regionData.getData();

        if (faction == null)
            return;

        if (faction.getAllMembers().contains(player.getUniqueId()))
            return;

        if (!this.blockedInteractions.contains(block.getType()))
            return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot do this in the territory of &c" + faction.getFactionName().get() + "&e."));
    }

    @EventHandler
    public void onEnderpearlInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL)) {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.ENDERPEARL);
                if (timer != null && timer.getTime() > 0) {
                    long millisLeft = timer.getTime();
                    double value = millisLeft / 1000.0D;
                    double sec = Math.round(10.0D * value) / 10.0D;
                    event.setCancelled(true);
                    event.getPlayer().updateInventory();
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this for another &c&l" + sec + "&c seconds."));
                    return;
                }
                Main.getInstance().getTimerHandler().addTimer(player, new DefaultTimer(TimerType.ENDERPEARL, 16000 + System.currentTimeMillis(), player));
            }
        }
    }
}
