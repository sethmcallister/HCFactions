package xyz.sethy.hcfactions.api.impl.claims;

import xyz.sethy.hcfactions.api.Location;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HCLocation implements Location {
    private final AtomicReference<UUID> worldId;
    private final AtomicInteger x;
    private final AtomicInteger y;
    private final AtomicInteger z;

    public HCLocation(final UUID uuid, final Integer x, final Integer y, final Integer z) {
        this.worldId = new AtomicReference<>(uuid);
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
        this.z = new AtomicInteger(z);
    }

    public AtomicReference<UUID> getWorldId() {
        return worldId;
    }

    public AtomicInteger getX() {
        return x;
    }

    public AtomicInteger getY() {
        return y;
    }

    public AtomicInteger getZ() {
        return z;
    }
}
