package xyz.sethy.hcfactions.command.factions;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FactionStuckCommand extends SubCommand implements Listener {
    private final List<Integer> warn;
    private final List<UUID> warping;
    private final List<UUID> damaged;

    public FactionStuckCommand() {
        super("stuck", new LinkedList<>(), true);
        this.warn = new LinkedList<>();
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        this.warping = new LinkedList<>();
        this.damaged = new LinkedList<>();

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction stuck");
            return;
        }
        final Player player = Bukkit.getPlayer(sender.getUniqueId());
        if (!Objects.equal(player.getWorld().getEnvironment(), World.Environment.NORMAL)) {
            sender.sendMessage("&cYou can only use this command in the over-world.");
            return;
        }
        Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.COMBAT_TAG);
        if (timer != null && timer.getTime() > 0L) {
            sender.sendMessage("&cYou cannot use this command while in combat.");
            return;
        }
        sender.sendMessage("&eYou will be teleported to safety in &a5 minutes&e.");

        Timer timer1 = new DefaultTimer(TimerType.F_STUCK, TimeUnit.MINUTES.toMillis(5L) + System.currentTimeMillis(), player);
        Main.getInstance().getTimerHandler().addTimer(player, timer1);

        new BukkitRunnable() {
            private int seconds = 300;
            private Location location = player.getLocation();
            private int xStart = (int) this.location.getX();
            private int yStart = (int) this.location.getY();
            private int zStart = (int) this.location.getZ();
            private Location nearest;

            @Override
            public void run() {
                Timer timer2 = Main.getInstance().getTimerHandler().getTimer(player, TimerType.F_STUCK);
                if (damaged.contains(sender.getUniqueId())) {
                    sender.sendMessage("&cYour teleportation has been cancelled because you toke damage.");
                    damaged.remove(sender.getUniqueId());
                    warping.remove(sender.getUniqueId());
                    Main.getInstance().getTimerHandler().getPlayerTimers(player).remove(timer2);
                    this.cancel();
                    return;
                }
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                if (this.seconds <= 0) {
                    if (this.nearest == null) {
                        player.setMetadata("SafelyLogout", new FixedMetadataValue(Main.getInstance(), true));
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                                "&cWe didn't find a safe location, so we kick you. Contact a staff member before logging back on. \n&eTS: &ats.ovidpvp.org"));
                        warping.remove(sender.getUniqueId());
                        this.cancel();
                        return;
                    }
                    player.teleport(this.nearest);
                    sender.sendMessage("&eYou have been teleported to the nearest safe location.");
                    warping.remove(sender.getUniqueId());
                    this.cancel();
                    return;
                }
                Location location = player.getLocation();
                if (location.getX() >= this.xStart + 5.0 || location.getX() <= this.xStart - 5.0 ||
                        location.getY() >= this.yStart + 5.0 || location.getY() <= this.yStart - 5.0 ||
                        location.getZ() >= this.zStart + 5.0 || location.getZ() <= this.zStart - 5.0) {
                    sender.sendMessage("&cYou have moved more than 5 blocks, so your teleportation was cancelled.");
                    Main.getInstance().getTimerHandler().getPlayerTimers(player).remove(timer2);
                    warping.remove(sender.getUniqueId());
                    this.cancel();
                }
                if (warn.contains(this.seconds)) {
                    sender.sendMessage("&eYou will be teleported to safety in &a" + this.seconds + "&e seconds.");
                }
                this.seconds--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
