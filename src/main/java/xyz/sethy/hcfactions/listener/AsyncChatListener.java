package xyz.sethy.hcfactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.ChatMode;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncChatListener implements Listener {
    private static Map<UUID, ChatMode> chatModes = new ConcurrentHashMap<>();

    public static void setChatMode(UUID uuid, ChatMode mode) {
        chatModes.put(uuid, mode);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        ChatMode chatMode = chatModes.get(event.getPlayer().getUniqueId());
        if (chatMode == null)
            chatMode = ChatMode.PUBLIC;

        Profile user = HCFAPI.getHCFManager().findProfileByUniqueId(event.getPlayer().getUniqueId());
        Faction faction = HCFAPI.getFactionManager().findByUser(user.getUniqueId());
        if (faction == null)
            chatMode = ChatMode.PUBLIC;

        event.setCancelled(true);
        switch (chatMode) {
            case PUBLIC: {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (faction == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', user.getBukkitColoredName() + " &6\u00bb&7 ") + event.getMessage());
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&f[" + (faction.getAllMembers().contains(player.getUniqueId()) ? "&a" : "&c") + faction.getFactionName().get() + "&f]" +
                                        user.getBukkitColoredName() +
                                        " &6\u00bb&7 ") +
                                event.getMessage());
                    }
                }
                break;
            }
            case FACTION: {
                for (UUID uuid : faction.getAllMembers()) {
                    Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    if (user1 != null) {
                        user1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&aFaction&f] " +
                                user.getName() +
                                " &6\u00bb&7 ") +
                                event.getMessage());
                    }
                }
                break;
            }
            case ALLY: {
                for (UUID uuid : faction.getAllMembers()) {
                    Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    if (user1 != null) {
                        user1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&bAlly&f] " +
                                user.getName() +
                                " &6\u00bb&7 ") +
                                event.getMessage());
                    }
                }
                for (UUID allyUUID : faction.getAllies()) {
                    if (allyUUID != null) {
                        Faction ally = HCFAPI.getFactionManager().findByUniqueId(allyUUID);
                        if (ally != null) {
                            for (UUID allyMember : ally.getAllMembers()) {
                                Profile user1 = HCFAPI.getHCFManager().findProfileByUniqueId(allyMember);
                                if (user1 != null) {
                                    user1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&bAlly&f] " +
                                            user.getName() +
                                            " &6\u00bb&7 ") +
                                            event.getMessage());
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}
