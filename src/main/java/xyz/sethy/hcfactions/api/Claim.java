package xyz.sethy.hcfactions.api;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface Claim {
    AtomicReference<UUID> getWorldUID();

    AtomicInteger getX1();

    AtomicInteger getY1();

    AtomicInteger getZ1();

    AtomicInteger getX2();

    AtomicInteger getY2();

    AtomicInteger getZ2();

    Location getMinimumPoint();

    Location getMaximumPoint();

    AtomicInteger getPrice();
}
