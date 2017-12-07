package xyz.sethy.hcfactions.command.factions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.claims.type.VisualClaimType;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.impl.claims.VisualClaim;

import java.util.LinkedList;

public class FactionClaimCommand extends SubCommand implements Listener {
    public FactionClaimCommand() {
        super("claim", new LinkedList<>(), true);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("&cUsage: /faction claim");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (args.length == 0) {
            if (faction == null) {
                sender.sendMessage("&cYou are not in a faction.");
                return;
            }
            if (!faction.isCaptainOrHigher(sender.getUniqueId())) {
                sender.sendMessage("&cYou must be at-least a captain to do this.");
                return;
            }
            Player player = Bukkit.getPlayer(sender.getUniqueId());
            player.getInventory().addItem(Main.getInstance().getItemHandler().getClaimWand());

            if (Main.getInstance().getVisualClaimHandler().getCurrentMaps().containsKey(player.getUniqueId())) {
                VisualClaim claim = Main.getInstance().getVisualClaimHandler().getCurrentMaps().get(player.getUniqueId());
                if (claim == null)
                    return;

                claim.cancel(true);
            }

            VisualClaim visualClaim = new VisualClaim(player, VisualClaimType.CREATE, true);
            Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(player.getUniqueId(), visualClaim);
        } else if (args[0].equalsIgnoreCase("--force")) {
            if (!sender.hasPermission("hcf.staff.admin")) {
                sender.sendMessage("&cYou do not have permission to execute this command.");
                return;
            }
            Player player = Bukkit.getPlayer(sender.getUniqueId());
            player.getInventory().addItem(Main.getInstance().getItemHandler().getClaimWand());

            if (Main.getInstance().getVisualClaimHandler().getCurrentMaps().containsKey(player.getUniqueId())) {
                VisualClaim claim = Main.getInstance().getVisualClaimHandler().getCurrentMaps().get(player.getUniqueId());
                if (claim == null)
                    return;

                claim.cancel(true);
            }

            VisualClaim visualClaim = new VisualClaim(player, VisualClaimType.CREATE, true);
            Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(player.getUniqueId(), visualClaim);
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(Main.getInstance().getItemHandler().getClaimWand())) {
            final VisualClaim visualClaim = Main.getInstance().getVisualClaimHandler().getVisualClaims().get(event.getPlayer().getUniqueId());
            if (visualClaim == null)
                return;

            event.getItemDrop().remove();
            visualClaim.cancel(false);
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getPlayer().getInventory().contains(Main.getInstance().getItemHandler().getClaimWand()))
            event.getPlayer().getInventory().remove(Main.getInstance().getItemHandler().getClaimWand());
    }
}
