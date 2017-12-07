package xyz.sethy.hcfactions.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Claim;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.FactionType;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlassWallTask extends BukkitRunnable {
    private final Map<UUID, List<Location>> map;

    public GlassWallTask() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeGlass(player);
            Location location = player.getLocation();
            if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.COMBAT_TAG)) {
                Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.COMBAT_TAG);
                if (timer.getTime() > 0) {
                    for (Map.Entry<Claim, Faction> claimMap : Main.getInstance().getClaimHandler().getRegionData(player.getLocation(), 10, 10, 10)) {
                        if (!claimMap.getValue().getFactionType().get().equals(FactionType.SAFE))
                            continue;

                        renderGlass(claimMap.getKey(), player, location);
                    }
                }
            }

            if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.PVP_TIMER)) {
                Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.PVP_TIMER);
                if (timer.getTime() > 0) {
                    for (Map.Entry<Claim, Faction> claimMap : Main.getInstance().getClaimHandler().getRegionData(player.getLocation(), 10, 10, 10)) {
                        if (claimMap.getValue().getFactionType().get().equals(FactionType.SAFE))
                            continue;

                        renderGlass(claimMap.getKey(), player, location);
                    }
                }
            }
        }
    }

    private void renderGlass(Claim claim, Player player, Location to) {
        if (claim == null)
            return;

        int x;
        int y;
        Location location;
        boolean updateZ;

        int closerx = closestNumber(to.getBlockX(), claim.getMinimumPoint().getX().get(), claim.getMaximumPoint().getX().get());
        int closerz = closestNumber(to.getBlockZ(), claim.getMinimumPoint().getZ().get(), claim.getMaximumPoint().getZ().get());
        boolean updateX = Math.abs(to.getX() - (double) closerx) < 10.0;
        boolean bl = updateZ = Math.abs(to.getZ() - (double) closerz) < 10.0;
        if (!updateX && !updateZ) {
            return;
        }
        List<Location> toUpdate = new LinkedList<>();
        if (updateX) {
            for (y = -2; y < 3; ++y) {
                for (x = -4; x < 4; ++x) {
                    if (!isInBetween(claim.getMinimumPoint().getZ().get(), claim.getMaximumPoint().getZ().get(), to.getBlockZ() + x) || toUpdate.contains(location = new Location(to.getWorld(), (double) closerx, (double) (to.getBlockY() + y), (double) (to.getBlockZ() + x))) || location.getBlock().getType().isOccluding())
                        continue;

                    toUpdate.add(location);
                }
            }
        }
        if (updateZ) {
            for (y = -2; y < 3; ++y) {
                for (x = -4; x < 4; ++x) {
                    if (!isInBetween(claim.getMinimumPoint().getX().get(), claim.getMaximumPoint().getX().get(), to.getBlockX() + x) || toUpdate.contains((location = new Location(to.getWorld(), (double) (to.getBlockX() + x), (double) (to.getBlockY() + y), (double) closerz))) || location.getBlock().getType().isOccluding())
                        continue;
                    toUpdate.add(location);
                }
            }
        }
        this.update(player, toUpdate);
    }

    private void update(Player player, List<Location> toUpdate) {
        if (this.map.containsKey(player.getUniqueId())) {
            this.map.get(player.getUniqueId()).addAll(toUpdate);
            for (Location location1 : toUpdate) {
                player.sendBlockChange(location1, 95, (byte) 14);
            }
        } else {
            for (Location location1 : toUpdate) {
                player.sendBlockChange(location1, 95, (byte) 14);
            }
            this.map.put(player.getUniqueId(), toUpdate);
        }
    }

    public void removeGlass(Player player) {
        if (this.map.containsKey(player.getUniqueId())) {
            for (Location location : this.map.get(player.getUniqueId())) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            this.map.remove(player.getUniqueId());
        }
    }

    private boolean isInBetween(int xone, int xother, int mid) {
        int distance = Math.abs(xone - xother);
        return distance == Math.abs(mid - xone) + Math.abs(mid - xother);
    }

    private int closestNumber(int from, int... numbers) {
        int distance = Math.abs(numbers[0] - from);
        int idx = 0;
        for (int c = 1; c < numbers.length; ++c) {
            int cdistance = Math.abs(numbers[c] - from);
            if (cdistance >= distance) continue;
            idx = c;
            distance = cdistance;
        }
        return numbers[idx];
    }
}
