package xyz.sethy.hcfactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.HCFProfile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);


        Player killed = event.getEntity();
        applyDeathban(killed);
        Player killer = killed.getKiller();

        Profile killedProfile = HCFAPI.getHCFManager().findProfileByUniqueId(killed.getUniqueId());
        killedProfile.setDeaths(killedProfile.getDeaths() + 1);

        Faction killedFaction = HCFAPI.getHCFManager().findByUser(killed.getUniqueId());
        double oldDTR = 0.0;
        if (killedFaction != null) {
            oldDTR = killedFaction.getDTR();
            killedFaction.setDTR(oldDTR - 1.0);
            if (killedFaction.isRaidable().get())
                killedFaction.getDTRFreeze().set(System.currentTimeMillis() + TimeUnit.MINUTES.toMinutes(60L));
        }

        if (killer == null) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                String factionColor = "&c";
                if (killedFaction != null) {
                    if (killedFaction.getAllMembers().contains(player.getUniqueId()))
                        factionColor = "&a";
                    else {
                        for (UUID uuid : killedFaction.getAllies()) {
                            Faction faction = HCFAPI.getHCFManager().findByUniqueId(uuid);
                            if (faction.getAllMembers().contains(player.getUniqueId()))
                                factionColor = "&b";
                        }
                    }
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', factionColor + killed.getName() + "&4[" + killedProfile.getKills() + "]&e has died."));
                if (factionColor.equalsIgnoreCase("&a")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMember Death: &c" + killed.getName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDTR Change: &c " + oldDTR + "&e ->&c " + killedFaction.getDTR()));
                }
            }
            return;
        }

        Profile killerProfile = HCFAPI.getHCFManager().findProfileByUniqueId(killer.getUniqueId());
        Faction killerFaction = HCFAPI.getHCFManager().findByUser(killer.getUniqueId());
        killerProfile.setKills(killerProfile.getKills() + 1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            String factionColor = "&c";
            String killerColor = "&c";

            if (killedFaction != null) {
                if (killedFaction.getAllMembers().contains(player.getUniqueId()))
                    factionColor = "&a";
                else {
                    for (UUID uuid : killedFaction.getAllies()) {
                        Faction faction = HCFAPI.getFactionManager().findByUniqueId(uuid);
                        if (faction.getAllMembers().contains(player.getUniqueId()))
                            factionColor = "&b";
                    }
                }
            }

            if (killerFaction != null) {
                if (killerFaction.getAllMembers().contains(player.getUniqueId())) {
                    killerColor = "&a";
                } else {
                    for (UUID uuid : killerFaction.getAllies()) {
                        Faction faction = HCFAPI.getFactionManager().findByUniqueId(uuid);
                        if (faction.getAllMembers().contains(player.getUniqueId()))
                            killerColor = "&b";
                    }
                }
            }

            addKillToItem(killer.getItemInHand(), killer, killed);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', factionColor + killed.getName() + "&4[" + killedProfile.getKills() + "]&e was slain by " + killerColor + killer.getName() + "&4[" + killerProfile.getKills() + "]"));
            if (factionColor.equalsIgnoreCase("&a")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMember Death: &c" + killed.getName()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDTR Change: &c " + oldDTR + "&e ->&c " + killedFaction.getDTR()));
            }
        }
    }

    private void applyDeathban(final Player player) {
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        profile.setDeathbanTime(Main.getInstance().getDeathbanHandler().getDeathbanTime(player));
        HCFAPI.getRedisProfileDAO().update((HCFProfile) profile);
    }

    private void addKillToItem(final ItemStack stack, final Player killer, final Player killed) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.translateAlternateColorCodes('&', String.format("&c%s &ewas slain by&c %s&e using &c%s&e.", killed.getName(), killer.getName(), killer.getItemInHand().toString())));
        meta.setLore(lore);
        stack.setItemMeta(meta);
    }
}
