package xyz.sethy.hcfactions.goose;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClass;
import xyz.sethy.hcfactions.api.impl.pvpclass.PvPClassType;
import xyz.sethy.hcfactions.impl.Koth;
import xyz.sethy.hcfactions.pvpclass.BardClass;
import xyz.sethy.hcfactions.timer.Timer;
import xyz.sethy.hcfactions.timer.TimerHandler;
import xyz.sethy.hcfactions.timer.TimerType;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class GooseTicker extends BukkitRunnable {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
    private TimerHandler timerHandler = Main.getInstance().getTimerHandler();
    private Map<Player, Long> lastPvPTime = new ConcurrentHashMap<>();

    public static String formatTime(long time) {
        if (time > 60000L)
            return setLongFormat(time);
        else
            return format(time);
    }

    private static String format(long millisecond) {
        return FORMAT.format(millisecond / 1000.0D);
    }

    private static String setLongFormat(long paramMilliseconds) {
        if (paramMilliseconds < TimeUnit.MINUTES.toMillis(1L))
            return FORMAT.format(paramMilliseconds);
        return DurationFormatUtils.formatDuration(paramMilliseconds,
                (paramMilliseconds >= TimeUnit.HOURS.toMillis(1L) ? "HH:" : "") +
                        "mm:ss");
    }

    private ConcurrentSkipListSet<String> splitEqually(final String text, final int size) {
        ConcurrentSkipListSet<String> ret = new ConcurrentSkipListSet<>();

        for (int start = 0; start < text.length(); start += size)
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        return ret;
    }

    private String translateString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GooseScoreboard scoreboard = Main.getInstance().getGooseHandler().getScoreboard(player);
            if (scoreboard == null)
                continue;

            scoreboard.clear();

            if (timerHandler.getPlayerTimers(player) == null) {
                timerHandler.getTimers().put(player.getUniqueId(), new LinkedList<>());
                scoreboard.update();
                continue;
            }

            if (hasAnyTimers(player)) {
                scoreboard.add(translateString("&7&m-----------"), translateString("&7&m-----------"));

                if (timerHandler.getSotwTime() > System.currentTimeMillis()) {
                    String right = translateString("&aSOTW&7:&f ");
                    String left = formatTime(timerHandler.getSotwTime() - System.currentTimeMillis());
                    scoreboard.add(right, left);
                }

                if (Main.getInstance().getPvPClassHandler().hasPvPClass(player)) {
                    PvPClass pvPClass = Main.getInstance().getPvPClassHandler().getPvPClassObj(player);
                    scoreboard.add(translateString("&6PvP Class&7:"), "");
                    scoreboard.add(translateString(" &8* "), translateString(pvPClass.getType().getScoreboardText()));
                    if (pvPClass.getType() == PvPClassType.BARD) {
                        scoreboard.add(translateString(" &8* &6Energy"), translateString("&8:&f ") + String.valueOf(((BardClass) pvPClass).getEnergy().get(player.getUniqueId())));
                    }
                }

                for (Koth koth : Main.getInstance().getKothHandler().getActiveKoths()) {
                    Faction faction = HCFAPI.getHCFManager().findByUniqueId(koth.getFactionId());
                    Timer timer = koth.getTimer();

                    if (timer.getTime() < 10L)
                        Main.getInstance().getKothHandler().setCaptured(faction);

                    String right = translateString(timer.getTimerType().getScore().replace("%koth_name%", faction.getFactionName().get().replace("koth", "")));
                    String left = translateString("&7: &f") + formatTime(timer.getTime());
                    scoreboard.add(right, left);
                }

                List<Timer> defaultTimers = timerHandler.getPlayerTimers(player);
                for (Timer timer : defaultTimers) {
                    if (timer.getTime() > 0) {
                        String left = translateString(timer.getTimerType().getScore());
                        String right;

                        if (timer.getTimerType().equals(TimerType.PVP_TIMER)) {
                            if (!timer.isFrozen()) {
                                if (this.lastPvPTime.containsKey(player))
                                    this.lastPvPTime.put(player, timer.getTime());
                                else
                                    this.lastPvPTime.put(player, 1800000L + System.currentTimeMillis());
                            }
                        }

                        if (timer.isFrozen()) {
                            if (!this.lastPvPTime.containsKey(player))
                                continue;
                            right = translateString("&7:&f ") + formatTime(this.lastPvPTime.get(player));
                        } else
                            right = translateString("&7:&f ") + formatTime(timer.getTime());

                        scoreboard.add(left, right);
                    }
                }
                scoreboard.add(translateString("&7&m-----------"), translateString("&7&m-----------"));
            }
            scoreboard.update();
        }
    }


    private boolean hasAnyTimers(Player player) {
        List<Timer> defaultTimers = timerHandler.getPlayerTimers(player);

        int i = (int) defaultTimers.stream().filter(timer -> timer.getTime() > 0).count();

        return i > 0 || timerHandler.getSotwTime() > System.currentTimeMillis() ||
                Main.getInstance().getPvPClassHandler().hasPvPClass(player) || !Main.getInstance().getKothHandler().getActiveKoths().isEmpty();
    }
}
