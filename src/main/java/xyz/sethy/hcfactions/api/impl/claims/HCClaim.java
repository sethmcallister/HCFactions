package xyz.sethy.hcfactions.api.impl.claims;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.api.Location;
import xyz.sethy.hcfactions.api.impl.claims.directions.CuboidDirection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HCClaim implements Claim, Iterable<Coordinate> {
    private final AtomicReference<UUID> worldUID;
    private final AtomicInteger x1;
    private final AtomicInteger y1;
    private final AtomicInteger z1;
    private final AtomicInteger x2;
    private final AtomicInteger y2;
    private final AtomicInteger z2;

    public HCClaim(final UUID world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.worldUID = new AtomicReference<>(world);
        this.x1 = new AtomicInteger(Math.min(x1, x2));
        this.x2 = new AtomicInteger(Math.max(x1, x2));
        this.y1 = new AtomicInteger(Math.min(y1, y2));
        this.y2 = new AtomicInteger(Math.max(y1, y2));
        this.z1 = new AtomicInteger(Math.min(z1, z2));
        this.z2 = new AtomicInteger(Math.max(z1, z2));
    }

    public AtomicReference<UUID> getWorldUID() {
        return worldUID;
    }

    public AtomicInteger getX1() {
        return x1;
    }

    public AtomicInteger getY1() {
        return y1;
    }

    public AtomicInteger getZ1() {
        return z1;
    }

    public AtomicInteger getX2() {
        return x2;
    }

    public AtomicInteger getY2() {
        return y2;
    }

    public AtomicInteger getZ2() {
        return z2;
    }

    public Location getMinimumPoint() {
        return new HCLocation(getWorldUID().get(), getX1().get(), getY1().get(), getZ1().get());
    }

    public Location getMaximumPoint() {
        return new HCLocation(getWorldUID().get(), getX2().get(), getY2().get(), getZ2().get());
    }

    public AtomicInteger getPrice() {
        final int x = Math.abs(x1.get() - x2.get());
        final int z = Math.abs(z1.get() - z2.get());
        int blocks = x * z;
        int done = 0;
        double mod = 0.4;
        double curPrice = 0.0D;
        while (blocks > 0) {
            blocks--;
            done++;
            curPrice += mod;
            if (done == 250) {
                done = 0;
                mod += 0.4;
            }
        }
        curPrice /= 2.0;
        return new AtomicInteger((int) (curPrice / 6));
    }

    public AtomicBoolean isWithin(final AtomicInteger x, final AtomicInteger z, final int radius, final UUID world) {
        return this.outset(CuboidDirection.BOTH, radius).contains(new HCLocation(world, x.get(), 100, z.get()));
    }

    public org.bukkit.Location[] getCornerLocations() {
        final World world = Bukkit.getServer().getWorld(this.getWorldUID().get());
        return new org.bukkit.Location[]{
                new org.bukkit.Location(world, (double) this.x1.get(), (double) this.y1.get(), (double) this.z1.get()),
                new org.bukkit.Location(world, (double) this.x2.get(), (double) this.y1.get(), (double) this.z2.get()),
                new org.bukkit.Location(world, (double) this.x1.get(), (double) this.y1.get(), (double) this.z2.get()),
                new org.bukkit.Location(world, (double) this.x2.get(), (double) this.y1.get(),
                        (double) this.z1.get())};
    }

    @Override
    public Claim clone() {
        return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get(), this.z1.get(), this.x2.get(),
                this.y2.get(), this.z2.get());
    }

    public boolean contains(final int x, final int y, final int z, final UUID world) {
        return this.y1 != null && y >= this.y1.get() && this.y2 != null && y <= this.y2.get() && this.contains(x, z, world);
    }

    public boolean contains(final int x, final int z, final UUID world) {
        return (world == null || Objects.equal(world, this.worldUID.get()) && x >= this.x1.get() && x <= this.x2.get() && z >= this.z1.get() && z <= this.z2.get());
    }

    public AtomicBoolean contains(final Location location) {
        return new AtomicBoolean(this.contains(location.getX().get(), location.getY().get(), location.getZ().get(),
                location.getWorldId().get()));
    }

    @Override
    public Iterator<Coordinate> iterator() {
        return new BorderIterator(this.x1.get(), this.z1.get(), this.x2.get(), this.z2.get());
    }

    private HCClaim expand(final CuboidDirection dir, final int amount) {
        switch (dir) {
            case NORTH: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get() - amount, this.y1.get(), this.z1.get(),
                        this.x2.get(), this.y2.get(), this.z2.get());
            }
            case SOUTH: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get(), this.z1.get(),
                        this.x2.get() + amount, this.y2.get(), this.z2.get());
            }
            case EAST: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get(), this.z1.get() - amount,
                        this.x2.get(), this.y2.get(), this.z2.get());
            }
            case WEST: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get(), this.z1.get(), this.x2.get(),
                        this.y2.get(), this.z2.get() + amount);
            }
            case DOWN: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get() - amount, this.z1.get(),
                        this.x2.get(), this.y2.get(), this.z2.get());
            }
            case UP: {
                return new HCClaim(this.getWorldUID().get(), this.x1.get(), this.y1.get(), this.z1.get(), this.x2.get(),
                        this.y2.get() + amount, this.z2.get());
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public HCClaim outset(final CuboidDirection dir, final int amount) {
        switch (dir) {
            case HORIZONTAL: {
                return this.expand(CuboidDirection.NORTH, amount)
                        .expand(CuboidDirection.SOUTH, amount)
                        .expand(CuboidDirection.EAST, amount)
                        .expand(CuboidDirection.WEST, amount);
            }
            case VERTICAL: {
                return this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
            }
            case BOTH: {
                return this.outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public enum BorderDirection {
        POS_X,
        POS_Z,
        NEG_X,
        NEG_Z
    }

    public class BorderIterator implements Iterator<Coordinate> {
        int maxX;
        int maxZ;
        int minX;
        int minZ;
        private int x;
        private int z;
        private boolean next;
        private BorderDirection dir;

        BorderIterator(final int x1, final int z1, final int x2, final int z2) {
            super();
            this.next = true;
            this.dir = BorderDirection.POS_Z;
            this.maxX = getMaximumPoint().getX().get();
            this.maxZ = getMaximumPoint().getZ().get();
            this.minX = getMinimumPoint().getX().get();
            this.minZ = getMinimumPoint().getZ().get();
            this.x = Math.min(x1, x2);
            this.z = Math.min(z1, z2);
        }

        @Override
        public boolean hasNext() {
            return this.next;
        }

        @Override
        public Coordinate next() {
            if (!next) {
                throw new NoSuchElementException();
            }
            if (this.dir == BorderDirection.POS_Z) {
                if (++this.z == this.maxZ) {
                    this.dir = BorderDirection.POS_X;
                }
            } else if (this.dir == BorderDirection.POS_X) {
                if (++this.x == this.maxX) {
                    this.dir = BorderDirection.NEG_Z;
                }
            } else if (this.dir == BorderDirection.NEG_Z) {
                if (--this.z == this.minZ) {
                    this.dir = BorderDirection.NEG_X;
                }
            } else if (this.dir == BorderDirection.NEG_X && --this.x == this.minX) {
                this.next = false;
            }
            return new Coordinate(this.x, this.z);
        }

        @Override
        public void remove() {
        }
    }
}