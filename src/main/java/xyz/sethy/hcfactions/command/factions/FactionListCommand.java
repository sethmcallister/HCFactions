package xyz.sethy.hcfactions.command.factions;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FactionListCommand extends SubCommand {
    public FactionListCommand() {
        super("list", new LinkedList<>(), false);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("&cUsage: /faction list <page#>");
            return;
        }
        TreeSet<Faction> factions = new TreeSet<>(new SizeComparator());
        for (Faction faction : HCFAPI.getFactionManager().findAll()) {
            if (faction != null && faction.getLeader().get() != null) {
                int size = 0;
                for (UUID uuid : faction.getAllMembers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline())
                        size++;
                }
                if (size > 0)
                    factions.add(faction);
            }
        }

        Map<Faction, Integer> factionMap = new ConcurrentHashMap<>();
        int i = 0;
        for (Faction faction : factions) {
            factionMap.put(faction, i);
            i++;
        }
        int page;
        int maxPages = factionMap.size() / 10;
        maxPages++;
        if (args.length != 1)
            page = 1;
        else
            page = Integer.parseInt(args[0]);

        if (page < 0) {
            sender.sendMessage("&cYou cannot view a list page less than 1.");
            return;
        }
        if (page > maxPages) {
            sender.sendMessage("&cYou cannot view a list page larger then " + maxPages + ".");
            return;
        }

        final int start = (page - 1) * 10;
        int index = 0;
        if (maxPages == 0) {
            sender.sendMessage("&cThere a no factions online.");
            return;
        }

        final Player player = Bukkit.getPlayer(sender.getUniqueId());
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eFaction List &fPage (#" + page + "/" + maxPages + ")");
        for (final Map.Entry<Faction, Integer> entry : factionMap.entrySet()) {
            if (index++ < start)
                continue;

            if (index > start + 10)
                break;

            int size = (int) entry.getKey().getAllMembers().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).count();

            new FancyMessage(" #" + 1).color(ChatColor.WHITE)
                    .then(" \u00bb ")
                    .color(ChatColor.GREEN)
                    .then(entry.getKey().getFactionName().get())
                    .color(entry.getKey()
                            .getAllMembers()
                            .contains(sender.getUniqueId()) ? ChatColor.GREEN : ChatColor.YELLOW)
                    .then(" (")
                    .color(ChatColor.WHITE)
                    .then(Integer.toString(size))
                    .color(ChatColor.GREEN)
                    .then("/")
                    .then(Integer.toString(entry.getKey().getAllMembers().size()))
                    .color(ChatColor.YELLOW)
                    .then(")")
                    .color(ChatColor.WHITE)
                    .then(" View ")
                    .color(ChatColor.YELLOW)
                    .then("information")
                    .color(ChatColor.GREEN)
                    .command("/f show " + entry.getKey().getFactionName())
                    .tooltip("Click here to view the faction's information")
                    .color(ChatColor.GRAY)
                    .then(".")
                    .send(player);
        }
        sender.sendMessage("&eTo view additional list pages, type &f/f list page#");
        sender.sendMessage("&e&m-----------------------------------------------------");
    }

    class SizeComparator implements Comparator<Faction> {
        @Override
        public int compare(Faction o1, Faction o2) {
            int size1 = 0;
            for (UUID uuid : o1.getAllMembers()) {
                if (Bukkit.getPlayer(uuid) != null)
                    size1++;
            }
            int size2 = 0;
            for (UUID uuid : o2.getAllMembers()) {
                if (Bukkit.getPlayer(uuid) != null)
                    size1++;
            }

            if (size1 < size2)
                return -1;
            if (size1 > size2)
                return 1;
            if (size1 == size2)
                return 0;
            return -1;
        }
    }
}
