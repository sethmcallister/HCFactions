package xyz.sethy.hcfactions.command.pvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.timer.DefaultTimer;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LogoutCommand implements CommandExecutor {
    private final Map<UUID, Integer> teleporting;

    public LogoutCommand() {
        this.teleporting = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can execute this command."));
            return true;
        }
        Player player = (Player) sender;
        if (Main.getInstance().getTimerHandler().hasTimer(player, TimerType.LOGOUT)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already logging out."));
            return true;
        }
        Timer timer = new DefaultTimer(TimerType.LOGOUT, TimeUnit.SECONDS.toMillis(30L) + System.currentTimeMillis(), player);
        Main.getInstance().getTimerHandler().addTimer(player, timer);
        startLogout(player);
        return true;
    }

    private void startLogout(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou will be logged out in &a30&e seconds."));
        this.teleporting.put(player.getUniqueId(), new BukkitRunnable() {
            int i = 29;

            @Override
            public void run() {
                if (!teleporting.containsKey(player.getUniqueId()))
                    return;

                if (i != 0) {
                    player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.C));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou will be logged out in &a" + i + "&e seconds."));
                    i--;
                    return;
                }

                player.setMetadata("SafelyLogout", new FixedMetadataValue(Main.getInstance(), true));
                player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&cYou have succesfully logged out."));
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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour logout was cancelled as you moved more than 1 block."));
            Timer timer = Main.getInstance().getTimerHandler().getTimer(player, TimerType.LOGOUT);
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
            damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour logout was cancelled as you took damage."));
            Timer timer = Main.getInstance().getTimerHandler().getTimer(damaged, TimerType.LOGOUT);
            if (timer != null)
                Main.getInstance().getTimerHandler().getPlayerTimers(damaged).remove(timer);
        }
    }
}
