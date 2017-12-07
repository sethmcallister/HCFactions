package xyz.sethy.hcfactions.handler;

import org.bukkit.Location;
import org.bukkit.Material;
import xyz.sethy.hcfactions.impl.claims.VisualClaim;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VisualClaimHandler {
    private final Material[] mapMaterials;
    private final Map<UUID, VisualClaim> currentMaps;
    private final Map<UUID, VisualClaim> visualClaims;
    private final Map<UUID, List<Location>> packetBlocksSent;
    private final Map<UUID, List<Location>> mapBlocksSent;

    public VisualClaimHandler() {
        this.mapMaterials = new Material[]{Material.DIAMOND_BLOCK, Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD, Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST, Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE, Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE};
        this.currentMaps = new ConcurrentHashMap<>();
        this.visualClaims = new ConcurrentHashMap<>();
        this.packetBlocksSent = new ConcurrentHashMap<>();
        this.mapBlocksSent = new ConcurrentHashMap<>();
    }

    public Material[] getMapMaterials() {
        return mapMaterials;
    }

    public Map<UUID, VisualClaim> getCurrentMaps() {
        return currentMaps;
    }

    public Map<UUID, VisualClaim> getVisualClaims() {
        return visualClaims;
    }

    public Map<UUID, List<Location>> getPacketBlocksSent() {
        return packetBlocksSent;
    }

    public Map<UUID, List<Location>> getMapBlocksSent() {
        return mapBlocksSent;
    }
}
