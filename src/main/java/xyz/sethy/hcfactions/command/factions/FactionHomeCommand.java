package xyz.sethy.hcfactions.command.factions;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.claims.RegionData;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FactionHomeCommand extends SubCommand implements Listener {
    private final Map<UUID, Integer> teleporting;

    public FactionHomeCommand() {
        super("home", Collections.singletonList("h"), true);
        this.teleporting = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction home");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction == null) {
            sender.sendMessage("&cYou are not in a faction.");
            return;
        }
        if (faction.getHome() == null) {
            sender.sendMessage("&cYour faction does not have a home set.");
            return;
        }
        Player player = Bukkit.getPlayer(sender.getUniqueId());
        RegionData region = Main.getInstance().getClaimHandler().getRegion(player.getLocation());
        if (region != null) {
            if (region.getData() != null && !region.getData().getAllMembers().contains(player.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot /f home inside enemy territory, please use /f stuck instead."));
                return;
            }
        }
        startTeleporting(player, faction);
        Timer timer = new DefaultTimer(TimerType.TELEPORT, TimeUnit.SECONDS.toMillis(10L) + System.currentTimeMillis(), player);
        Main.getInstance().getTimerHandler().addTimer(player, timer);
    }

    private void startTeleporting(Player player, Faction faction) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou will be teleported to your faction home in &a10&e seconds."));
        this.teleporting.put(player.getUniqueId(), new BukkitRunnable() {
            int i = 9;

            @Override
            public void run() {
                if (!teleporting.containsKey(player.getUniqueId()))
                    return;

                if (i != 0) {
                    player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.C));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou will be teleported to your faction home in &a" + i + "&e seconds."));
                    i--;
                    return;
                }
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                if (faction.getHome() == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
                    return;
                }
                xyz.sethy.hcfactions.api.Location home = faction.getHome();
                org.bukkit.Location location = new org.bukkit.Location(Bukkit.getWorld(home.getWorldId().get()), home.getX().get(),
                        home.getY().get(), home.getZ().get());
                player.teleport(location);
                teleporting.remove(player.getUniqueId());
                cancel();
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L).getTaskId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        final Player player = event.getPlayer();

        if (teleporting.containsKey(event.getPlayer().getUniqueId())) {
            int runnable = teleporting.get(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().cancelTask(runnable);
            teleporting.remove(event.getPlayer().getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour teleportation was cancelled as you moved more than 1 block."));
            Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.TELEPORT);
            if (timer != null)
                Main.getInstance().getTimerHandler().getPlayerTimers(player).remove(timer);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player damaged = (Player) event.getEntity();
        if (this.teleporting.containsKey(damaged.getUniqueId())) {
            int runnable = teleporting.get(damaged.getUniqueId());
            Bukkit.getScheduler().cancelTask(runnable);
            teleporting.remove(damaged.getUniqueId());
            damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour teleportation was cancelled as you took damage."));
            Timer timer = Main.getInstance().getTimerHandler().getTimer(damaged, TimerType.TELEPORT);
            if (timer != null)
                Main.getInstance().getTimerHandler().getPlayerTimers(damaged).remove(timer);
        }
    }
}
