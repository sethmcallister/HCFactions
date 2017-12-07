package xyz.sethy.hcfactions.api;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface Location {
    AtomicReference<UUID> getWorldId();

    AtomicInteger getX();

    AtomicInteger getY();

    AtomicInteger getZ();
}
