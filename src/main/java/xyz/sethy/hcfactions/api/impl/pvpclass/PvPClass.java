package xyz.sethy.hcfactions.api.impl.pvpclass;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class PvPClass {
    private final List<UUID> players;
    private final PvPClassType type;

    public PvPClass(PvPClassType type) {
        this.players = new LinkedList<>();
        this.type = type;
    }

    public abstract void onEquip(Player player);

    public abstract void onUnEquip(Player player);

    public List<UUID> getPlayers() {
        return players;
    }

    public PvPClassType getType() {
        return type;
    }
}
