package xyz.sethy.hcfactions.api.impl.claims;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;

public class RegionData {
    private RegionType regionType;
    private Faction data;

    public RegionData(final RegionType regionType, final Faction data) {
        super();
        this.regionType = regionType;
        this.data = data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof RegionData))
            return false;
        final RegionData other = (RegionData) obj;
        return other.regionType == this.regionType && (this.data == null || other.data.equals(this.data));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getName(final Player player) {
        if (this.data != null) {
            if (this.getData().getAllMembers().contains(player.getUniqueId()))
                return ChatColor.GREEN + this.data.getFactionName().get();

            return ChatColor.RED + this.data.getFactionName().get();
        }
        switch (this.regionType) {
            case WARZONE:
                return ChatColor.RED + "Warzone";
            case WILDERNESS:
                return ChatColor.GRAY + "The Wilderness";
            default:
                return ChatColor.DARK_RED + "N/A";
        }
    }

    public RegionType getRegionType() {
        return this.regionType;
    }

    public void setRegionType(final RegionType regionType) {
        this.regionType = regionType;
    }

    public Faction getData() {
        return this.data;
    }

    public void setData(final Faction data) {
        this.data = data;
    }
}
