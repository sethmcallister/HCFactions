package xyz.sethy.hcfactions.handler;

import org.bukkit.Location;
import org.bukkit.World;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.FactionType;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.claims.HCClaim;
import xyz.sethy.hcfactions.api.impl.claims.HCLocation;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.api.impl.claims.RegionType;
import xyz.sethy.hcfactions.impl.claims.VisualClaim;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimHandler {
    private final int WARZONE_RADIUS;
    private Map<Claim, Faction> boardMap;

    public ClaimHandler() {
        this.WARZONE_RADIUS = 500;
        this.boardMap = new ConcurrentHashMap<>();
        loadFromFactions();
    }

    private void loadFromFactions() {
        for (Map.Entry<Integer, Faction> entry : HCFAPI.getHCFManager().findAllFactions().entrySet()) {
            Faction faction = entry.getValue();
            if (faction.getFactionClaim() != null)
                this.setFactionAt(faction.getFactionClaim(), faction);
        }
    }

    public Set<Map.Entry<Claim, Faction>> getRegionData(final Location center, final int xDistance, final int yDistance, final int zDistance) {
        final Location loc1 = new Location(center.getWorld(), (double) (center.getBlockX() - xDistance), (double) (center.getBlockY() - yDistance), (double) (center.getBlockZ() - zDistance));
        final Location loc2 = new Location(center.getWorld(), (double) (center.getBlockX() + xDistance), (double) (center.getBlockY() + yDistance), (double) (center.getBlockZ() + zDistance));
        return this.getRegionData(loc1, loc2);
    }

    private Set<Map.Entry<Claim, Faction>> getRegionData(final Location min, final Location max) {
        final Set<Map.Entry<Claim, Faction>> regions = new HashSet<>();
        for (final Map.Entry<Claim, Faction> regionEntry : this.boardMap.entrySet()) {
            if (regionEntry.getValue() != null &&
                    regionEntry.getKey() != null &&
                    !regions.contains(regionEntry) &&
                    max.getWorld().getUID().equals(regionEntry.getKey().getWorldUID().get()) &&
                    max.getBlockX() >= regionEntry.getKey().getX1().get() &&
                    min.getBlockX() <= regionEntry.getKey().getX2().get() &&
                    max.getBlockZ() >= regionEntry.getKey().getZ1().get() &&
                    min.getBlockZ() <= regionEntry.getKey().getZ2().get() &&
                    max.getBlockY() >= regionEntry.getKey().getY1().get() &&
                    min.getBlockY() <= regionEntry.getKey().getY2().get()) {
                if (regionEntry.getValue().getFactionClaim() != null && regionEntry.getValue().getFactionClaim().equals(regionEntry.getKey()))
                    regions.add(regionEntry);
            }
        }
        return regions;
    }

    public RegionData getRegion(final Location location) {
        return this.getRegion(Main.getInstance().getClaimHandler().getFaction(location), location);
    }

    public RegionData getRegion(final Faction to, final Location location) {
        if (to != null && to.getLeader() == null) {
            if (to.getFactionType().get().equals(FactionType.SAFE))
                return new RegionData(RegionType.SPAWN, to);
            if (to.getFactionType().get().equals(FactionType.ROAD))
                return new RegionData(RegionType.ROAD, to);
        }
        if (to != null)
            return new RegionData(RegionType.CLAIMED_LAND, to);

        if (Main.getInstance().getClaimHandler().isWarzone(location))
            return new RegionData(RegionType.WARZONE, null);

        return new RegionData(RegionType.WILDERNESS, null);
    }

    public Map.Entry<Claim, Faction> getRegionData(final Location location) {
        for (final Map.Entry<Claim, Faction> entry : this.boardMap.entrySet()) {
            xyz.sethy.hcfactions.api.Location location1 = new HCLocation(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (entry.getKey() != null && ((HCClaim) entry.getKey()).contains(location1).get()) {
                if (entry.getValue() != null && entry.getValue().getFactionClaim() != null && entry.getValue().getFactionClaim().equals(entry.getKey())) {
                    return entry;
                }
            }
        }
        return null;
    }

    public Claim getClaim(final Location location) {
        final Map.Entry<Claim, Faction> regionData = this.getRegionData(location);

        return (regionData == null) ? null : regionData.getKey();
    }

    private Faction getFaction(final Location location) {
        final Map.Entry<Claim, Faction> regionData = this.getRegionData(location);
        return (regionData == null) ? null : regionData.getValue();
    }

    public void setFactionAt(final Claim claim, final Faction faction) {
        if (claim == null) {
            Claim claim1 = faction.getFactionClaim();
            this.boardMap.remove(claim1);
            return;
        }

        if (faction == null)
            this.boardMap.remove(claim);
        else
            this.boardMap.put(claim, faction);
        this.updateClaim(claim);
    }

    private void updateClaim(final Claim modified) {
        final List<VisualClaim> visualClaims = new LinkedList<>();
        visualClaims.addAll(Main.getInstance().getVisualClaimHandler().getCurrentMaps().values());
        for (final VisualClaim visualClaim : visualClaims) {
//            if (modified != null && ((HCClaim) modified).isWithin(new AtomicInteger(visualClaim.getPlayer().getLocation().getBlockX()), new AtomicInteger(visualClaim.getPlayer().getLocation().getBlockZ()), 50, modified.getWorldUID().get()).get())
//                visualClaim.draw(true);
//                visualClaim.draw(true);
        }
    }

    public void clear(final Faction faction) {
        if (faction.getFactionClaim() != null) {
            this.boardMap.remove(faction.getFactionClaim());
        }
    }

    public boolean isUnclaimed(final Location location) {
        return getClaim(location) == null && !this.isWarzone(location);
    }

    private boolean isWarzone(final Location loc) {
        return loc.getWorld().getEnvironment() == World.Environment.NORMAL && Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS;
    }

    public boolean isSpawnBufferZone(final Location loc) {
        if (loc.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return false;
        }
        final int radius = 150;
        final int x = loc.getBlockX();
        final int z = loc.getBlockZ();
        return x < radius && x > -radius && z < radius && z > -radius;
    }

    public boolean isNetherBufferZone(final Location loc) {
        if (loc.getWorld().getEnvironment() != World.Environment.NETHER)
            return false;
        final int radius = 150;
        final int x = loc.getBlockX();
        final int z = loc.getBlockZ();
        return x < radius && x > -radius && z < radius && z > -radius;
    }

    public boolean isUnclaimedOrRaidable(final Location loc) {
        final Faction owner = getFaction(loc);
        return owner == null || owner.isRaidable().get();
    }
}
