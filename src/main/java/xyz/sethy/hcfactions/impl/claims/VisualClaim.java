package xyz.sethy.hcfactions.impl.claims;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.claims.Coordinate;
import xyz.sethy.hcfactions.api.impl.claims.HCClaim;
import xyz.sethy.hcfactions.api.impl.claims.directions.CuboidDirection;
import xyz.sethy.hcfactions.api.impl.claims.type.VisualClaimType;
import xyz.sethy.hcfactions.impl.Koth;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VisualClaim implements Listener {
    private Player player;
    private VisualClaimType type;
    private boolean bypass;
    private Location corner1;
    private Location corner2;
    private Koth koth;

    public VisualClaim(final Player player, final VisualClaimType type, final boolean bypass) {
        if (player == null)
            throw new NullPointerException("player");
        if (type == null)
            throw new NullPointerException("type");

        this.player = player;
        this.type = type;
        this.bypass = bypass;

        if (Main.getInstance().getVisualClaimHandler().getCurrentMaps().containsKey(this.player.getUniqueId()) && this.type == VisualClaimType.MAP) {
            VisualClaim visualClaim = Main.getInstance().getVisualClaimHandler().getCurrentMaps().get(this.player.getUniqueId());
            visualClaim.cancel(true);
            System.out.println("already map to returning");
            return;
        }

        if (Main.getInstance().getVisualClaimHandler().getVisualClaims().containsKey(this.player.getUniqueId()) && (this.type == VisualClaimType.CREATE || this.type == VisualClaimType.RESIZE))
            Main.getInstance().getVisualClaimHandler().getVisualClaims().get(this.player.getUniqueId()).cancel(true);

        switch (this.type) {
            case CREATE:
            case RESIZE:
            case KOTH: {
                Main.getInstance().getVisualClaimHandler().getVisualClaims().put(this.player.getUniqueId(), this);
                break;
            }
            case MAP: {
                Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(this.player.getUniqueId(), this);
                break;
            }
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, HCFAPI.getPlugin());

        switch (this.type) {
            case KOTH:
            case CREATE:
                break;
            case MAP: {
                int claimIteration = 0;
                final Map<Map.Entry<Claim, Faction>, Material> sendMaps = new ConcurrentHashMap<>();
                for (final Map.Entry<Claim, Faction> regionData : Main.getInstance().getClaimHandler().getRegionData(this.player.getLocation(), 50, 256, 50)) {
                    final Material mat = this.getMaterial(claimIteration);
                    ++claimIteration;
                    if (regionData.getKey() != null) {
                        this.drawClaim(regionData.getKey(), mat);
                        sendMaps.put(regionData, mat);
                    }
                }
                if (sendMaps.isEmpty()) {
                    this.player.sendMessage(ChatColor.YELLOW + "There are no claims within " + ChatColor.RED + 50 + ChatColor.YELLOW + " blocks of you!");

                    this.cancel(true);
                }
                for (final Map.Entry<Map.Entry<Claim, Faction>, Material> claim : sendMaps.entrySet()) {
                    if (claim.getKey().getValue() != null) {
                        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThe faction &f" +
                                claim.getKey()
                                        .getValue()
                                        .getFactionName() +
                                "&e owns the land &f(" +
                                claim.getValue()
                                        .name() + ")"));
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public VisualClaim(final Player player, final VisualClaimType type, Koth koth, final boolean bypass) {
        if (player == null)
            throw new NullPointerException("player");
        if (type == null)
            throw new NullPointerException("type");

        this.player = player;
        this.type = type;
        this.koth = koth;
        this.bypass = bypass;

        if (Main.getInstance().getVisualClaimHandler().getCurrentMaps().containsKey(this.player.getUniqueId()) && this.type == VisualClaimType.MAP) {
            Main.getInstance().getVisualClaimHandler().getCurrentMaps().get(this.player.getUniqueId()).cancel(true);
            return;
        }
        if (Main.getInstance().getVisualClaimHandler().getVisualClaims().containsKey(this.player.getUniqueId()) && (this.type == VisualClaimType.CREATE || this.type == VisualClaimType.RESIZE))
            Main.getInstance().getVisualClaimHandler().getVisualClaims().get(this.player.getUniqueId()).cancel(true);
        switch (this.type) {
            case CREATE:
            case RESIZE:
            case KOTH: {
                Main.getInstance().getVisualClaimHandler().getVisualClaims().put(this.player.getUniqueId(), this);
                break;
            }
            case MAP: {
                Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(this.player.getUniqueId(), this);
                break;
            }
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, HCFAPI.getPlugin());

        switch (this.type) {
            case KOTH:
            case CREATE:
                break;
            case MAP: {
                int claimIteration = 0;
                final Map<Map.Entry<Claim, Faction>, Material> sendMaps = new ConcurrentHashMap<>();
                for (final Map.Entry<Claim, Faction> regionData : Main.getInstance().getClaimHandler().getRegionData(this.player.getLocation(), 50, 256, 50)) {
                    final Material mat = this.getMaterial(claimIteration);
                    ++claimIteration;
                    if (regionData.getKey() != null) {
                        this.drawClaim(regionData.getKey(), mat);
                        sendMaps.put(regionData, mat);
                    }
                }
                if (sendMaps.isEmpty()) {
                    this.player.sendMessage(ChatColor.YELLOW + "There are no claims within " + ChatColor.RED + 50 + ChatColor.YELLOW + " blocks of you!");

                    this.cancel(true);
                }
                for (final Map.Entry<Map.Entry<Claim, Faction>, Material> claim : sendMaps.entrySet()) {
                    if (claim.getKey().getValue() != null) {
                        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThe faction &f" +
                                claim.getKey()
                                        .getValue()
                                        .getFactionName() +
                                "&e owns the land &f(" +
                                claim.getValue()
                                        .name() + ")"));
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean containsOtherClaim(final Claim claim) {
        Location maxPoint = new Location(Bukkit.getWorld(claim.getMaximumPoint().getWorldId().get()), claim.getMaximumPoint().getX().get(), claim.getMaximumPoint().getY().get(), claim.getMaximumPoint().getZ().get());
        if (!Main.getInstance().getClaimHandler().isUnclaimed(maxPoint))
            return true;

        Location minPoint = new Location(Bukkit.getWorld(claim.getMinimumPoint().getWorldId().get()),
                claim.getMinimumPoint().getX().get(), claim.getMinimumPoint().getY().get(),
                claim.getMinimumPoint().getZ().get());
        if (!Main.getInstance().getClaimHandler().isUnclaimed(minPoint))
            return true;

        if (Math.abs(claim.getX1().get() - claim.getX2().get()) == 0 || Math.abs(claim.getZ1().get() - claim.getZ2().get()) == 0)
            return false;

        for (final Coordinate location : (HCClaim) claim) {
            if (!Main.getInstance().getClaimHandler().isUnclaimed(new Location(Bukkit.getServer().getWorld(claim.getWorldUID().get()), (double) location.getX(), 80.0, (double) location.getZ())))
                return true;
        }
        return false;
    }

    private Set<HCClaim> touchesOtherClaim(final Claim claim) {
        final Set<HCClaim> touchingClaims = new HashSet<>();
        for (final Coordinate coordinate : ((HCClaim) claim).outset(CuboidDirection.HORIZONTAL, 1)) {
            final Location loc = new Location(Bukkit.getServer().getWorld(claim.getWorldUID().get()),
                    (double) coordinate.getX(), 80.0, (double) coordinate.getZ());
            final HCClaim cc = (HCClaim) Main.getInstance().getClaimHandler().getClaim(loc);
            if (cc != null)
                touchingClaims.add(cc);
        }
        return touchingClaims;
    }

    private void setLoc(final int locationId, final Location clicked) {
        final Faction playerFaction = HCFAPI.getHCFManager().findByUser(this.player.getUniqueId());
        if (playerFaction == null) {
            this.player.sendMessage(ChatColor.RED + "You have to be in a faction to claim land.");
            this.cancel(true);
            return;
        }
        if (locationId == 1) {
            if (this.corner2 != null && this.isIllegal(new HCClaim(clicked.getWorld().getUID(), clicked.getBlockX(), clicked.getBlockY(), clicked.getBlockZ(), this.corner2.getBlockX(), this.corner2.getBlockY(), this.corner2.getBlockZ())))
                return;

            this.clearPillarAt(this.corner1);
            this.corner1 = clicked;
        } else if (locationId == 2) {
            if (this.corner1 != null && this.corner2 != null && this.isIllegal(new HCClaim(corner1.getWorld().getUID(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ(),
                    clicked.getBlockX(), clicked.getBlockY(), clicked.getBlockZ())))
                return;
            this.clearPillarAt(this.corner2);
            this.corner2 = clicked;
        }
        this.player.sendMessage(
                ChatColor.YELLOW + "Set your claim's location " + ChatColor.GREEN + locationId + ChatColor.YELLOW +
                        " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() +
                        ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> this.erectPillar(clicked, Material.EMERALD_BLOCK), 1L);
        final int price = this.getPrice();
        if (price != -1) {
            final int x = Math.abs(this.corner1.getBlockX() - this.corner2.getBlockX());
            final int z = Math.abs(this.corner1.getBlockZ() - this.corner2.getBlockZ());

            if (price > playerFaction.getBalance() && !this.bypass)
                this.player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW +
                        ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " +
                        ChatColor.WHITE + x * z + ChatColor.YELLOW + " blocks");
            else
                this.player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + x * z + ChatColor.YELLOW + " blocks");
        }
    }

    public void cancel(final boolean complete) {
        if (complete && (this.type == VisualClaimType.CREATE || this.type == VisualClaimType.RESIZE)) {
            this.clearPillarAt(this.corner1);
            this.clearPillarAt(this.corner2);
        }
        if (this.type == VisualClaimType.CREATE || this.type == VisualClaimType.RESIZE)
            this.player.getInventory().remove(Main.getInstance().getItemHandler().getClaimWand());

        HandlerList.unregisterAll(this);
        switch (this.type) {
            case MAP: {
                Main.getInstance().getVisualClaimHandler().getCurrentMaps().remove(this.player.getUniqueId());
                if (Main.getInstance().getVisualClaimHandler().getMapBlocksSent().containsKey(this.player.getUniqueId()))
                    Main.getInstance().getVisualClaimHandler().getMapBlocksSent().get(this.player.getUniqueId()).forEach(l -> this.player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));

                Main.getInstance().getVisualClaimHandler().getMapBlocksSent().remove(this.player.getUniqueId());
                break;
            }
            case KOTH:
            case CREATE:
            case RESIZE: {
                Main.getInstance().getVisualClaimHandler().getVisualClaims().remove(this.player.getUniqueId());
                break;
            }
        }
        if (Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().containsKey(this.player.getUniqueId())) {
            Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().get(this.player.getUniqueId())
                    .forEach(l -> this.player.sendBlockChange(l, l.getBlock().getType(),
                            l.getBlock().getData()));
        }
        Main.getInstance().getVisualClaimHandler().getCurrentMaps().remove(this.player.getUniqueId());
        Main.getInstance().getVisualClaimHandler().getMapBlocksSent().remove(this.player.getUniqueId());
        Main.getInstance().getVisualClaimHandler().getVisualClaims().remove(this.player.getUniqueId());
        Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().remove(this.player.getUniqueId());
    }

    private void handlePurchase() {
        if (this.type.equals(VisualClaimType.KOTH)) {
            Koth koth = this.koth;
            final Claim claim = new HCClaim(corner1.getWorld().getUID(), this.corner1.getBlockX(),
                    this.corner1.getBlockY(), this.corner1.getBlockZ(),
                    this.corner2.getBlockX(), this.corner2.getBlockY(),
                    this.corner2.getBlockZ());

            claim.getY1().set(this.corner1.getBlockY());
            claim.getY2().set(this.corner2.getBlockY());

            koth.setClaim(claim);
            Faction faction = HCFAPI.getHCFManager().findByUniqueId(koth.getFactionId());
            Main.getInstance().getClaimHandler().setFactionAt(claim, faction);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have successfully created the KoTH &a" + faction.getFactionName() + "&e."));
            this.cancel(true);
            return;
        }
        final Faction faction = HCFAPI.getFactionManager().findByUser(this.player.getUniqueId());
        if (faction == null) {
            this.player.sendMessage(ChatColor.RED + "You have to be in a faction to claim land!");
            this.cancel(true);
            return;
        }
        if (this.corner1 != null && this.corner2 != null) {
            final int price = this.getPrice();
            if (!this.bypass && faction.getFactionClaim() != null) {
                this.player.sendMessage(
                        ChatColor.RED + "Your faction has the maximum amount of claims, which is " + 1 + ".");
                return;
            }
            if (!this.bypass && !faction.getLeader().get().equals(this.player.getUniqueId())) {
                this.player.sendMessage(ChatColor.RED + "Only faction captains can claim land.");
                return;
            }
            if (!this.bypass && faction.getBalance() < price) {
                this.player.sendMessage(ChatColor.RED + "Your faction does not have enough money to do this!");
                return;
            }
            if (!this.bypass && faction.isRaidable().get()) {
                this.player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                return;
            }

            final Claim claim = new HCClaim(corner1.getWorld().getUID(), this.corner1.getBlockX(),
                    this.corner1.getBlockY(), this.corner1.getBlockZ(),
                    this.corner2.getBlockX(), this.corner2.getBlockY(),
                    this.corner2.getBlockZ());

            if (this.isIllegal(claim))
                return;

            claim.getY1().set(0);
            claim.getY2().set(256);

            Main.getInstance().getClaimHandler().setFactionAt(claim, faction);
            faction.setClaim(claim);
            this.player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your faction!");
            if (!this.bypass) {
                faction.setBalance(faction.getBalance() - price);
                this.player.sendMessage(
                        ChatColor.YELLOW + "Your factions now has " + ChatColor.GREEN + "$" + faction.getBalance() +
                                ChatColor.WHITE + " (Price: " + "$" + price);
            }
            this.cancel(true);
        } else
            this.player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
    }

    private int getPrice() {
        if (this.corner1 == null || this.corner2 == null)
            return -1;

        return new HCClaim(this.corner1.getWorld().getUID(), corner1.getBlockX(), corner1.getBlockY(),
                corner1.getBlockZ(), corner2.getBlockX(), corner2.getBlockY(),
                corner2.getBlockZ()).getPrice().get();
    }

    private void drawClaim(final Claim claim, final Material material) {
        for (final Location loc : ((HCClaim) claim).getCornerLocations())
            this.erectPillar(loc, material);
    }

    private void erectPillar(final Location loc, final Material mat) {
        final Location set = loc.clone();
        List<Location> locs = new ArrayList<>();
        if (this.type == VisualClaimType.MAP) {
            if (Main.getInstance().getVisualClaimHandler().getMapBlocksSent().containsKey(this.player.getUniqueId()))
                locs = Main.getInstance().getVisualClaimHandler().getMapBlocksSent().get(this.player.getUniqueId());
        } else if (Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().containsKey(this.player.getUniqueId()))
            locs = Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().get(this.player.getUniqueId());
        for (int i = 0; i < 256; ++i) {
            set.setY((double) i);
            if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent()) {
                if (i % 5 == 0)
                    this.player.sendBlockChange(set, mat, (byte) 0);
                else
                    this.player.sendBlockChange(set, Material.GLASS, (byte) 0);
                locs.add(set.clone());
            }
        }
        if (this.type == VisualClaimType.MAP)
            Main.getInstance().getVisualClaimHandler().getMapBlocksSent().put(this.player.getUniqueId(), locs);
        else
            Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().put(this.player.getUniqueId(), locs);
    }

    private void clearPillarAt(final Location loc) {
        if (Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().containsKey(this.player.getUniqueId()) && loc != null) {
            Main.getInstance().getVisualClaimHandler().getPacketBlocksSent().get(this.player.getUniqueId()).removeIf(l ->
            {
                if (l.getBlockX() == loc.getBlockX() && l.getBlockZ() == loc.getBlockZ()) {
                    this.player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
                    return true;
                }
                return false;
            });
        }
    }

    private boolean isIllegal(final Claim claim) {
        final Faction faction = HCFAPI.getHCFManager().findByUser(this.player.getUniqueId());
        if (!this.bypass && this.containsOtherClaim(claim)) {
            this.player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
            return true;
        }
        if (!this.bypass && this.player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            this.player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
            return true;
        }
        final Set<HCClaim> touching = this.touchesOtherClaim(claim);
        final Set<Claim> cloneCheck = new HashSet<>();

        touching.forEach(tee -> cloneCheck.add(tee.clone()));


        final boolean contains = faction.getFactionClaim() != null && faction.getFactionClaim().equals(claim);

        if (!this.bypass && faction.getFactionClaim() != null && !contains) {
            this.player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
            return true;
        }
        if (!this.bypass && (touching.size() > 1 || (touching.size() == 1 && !contains))) {
            this.player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
            return true;
        }
        final int x = Math.abs(claim.getX1().get() - claim.getX2().get());
        final int z = Math.abs(claim.getZ1().get() - claim.getZ2().get());
        if (!this.bypass && (x < 4 || z < 4)) {
            this.player.sendMessage(ChatColor.YELLOW + "Your claim is too small! The claim has to be at least (" + ChatColor.RED + "5 x 5" + ChatColor.YELLOW + ")");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getPlayer() == this.player && (this.type == VisualClaimType.CREATE || this.type == VisualClaimType.RESIZE || this.type == VisualClaimType.KOTH) && this.player.getItemInHand() != null && this.player.getItemInHand().getType() == Material.GOLD_HOE) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!this.bypass && !Main.getInstance().getClaimHandler().isUnclaimed(event.getClickedBlock().getLocation())) {
                    this.player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                    return;
                }
                this.setLoc(2, event.getClickedBlock().getLocation());
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!this.bypass && !Main.getInstance().getClaimHandler().isUnclaimed(event.getClickedBlock().getLocation())) {
                    this.player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                    return;
                }
                if (this.player.isSneaking())
                    this.handlePurchase();
                else
                    this.setLoc(1, event.getClickedBlock().getLocation());
            } else if (event.getAction() == Action.LEFT_CLICK_AIR && this.player.isSneaking())
                this.handlePurchase();
            else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                this.cancel(false);
                this.player.sendMessage(ChatColor.RED + "You have unset your first and second locations!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        if (this.player == e.getPlayer())
            this.cancel(true);
    }

    private Material getMaterial(int iteration) {
        if (iteration == -1)
            return Material.IRON_BLOCK;

        while (iteration >= Main.getInstance().getVisualClaimHandler().getMapMaterials().length)
            iteration -= Main.getInstance().getVisualClaimHandler().getMapMaterials().length;
        return Main.getInstance().getVisualClaimHandler().getMapMaterials()[iteration];
    }

    public Player getPlayer() {
        return this.player;
    }
}
