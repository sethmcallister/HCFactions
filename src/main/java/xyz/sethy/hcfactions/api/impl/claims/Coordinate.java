package xyz.sethy.hcfactions.api.impl.claims;

public class Coordinate {
    int x;
    int z;

    public Coordinate(final int x, final int z) {
        super();
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Coordinate)) {
            return false;
        }

        final Coordinate other = (Coordinate) o;
        return other.canEqual(this) && this.getX() == other.getX() && this.getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return x + z;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Coordinate;
    }
}
