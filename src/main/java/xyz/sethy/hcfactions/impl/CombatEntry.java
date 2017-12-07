package xyz.sethy.hcfactions.impl;

import net.techcable.npclib.NPC;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CombatEntry extends BukkitRunnable implements Listener {
    private UUID uuid;
    private String name;
    private NPC npc;
    private long deathBanTime;
    private BukkitTask bukkitTask;
    private int i = 30;

    public CombatEntry(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.deathBanTime = Main.getInstance().getDeathbanHandler().getDeathbanTime(player);

        this.deathBanTime = TimeUnit.HOURS.toMillis(2L);

        this.npc = Main.getInstance()
                .getCombatLogHandler()
                .getNpcRegistry()
                .createNPC(EntityType.PLAYER, this.uuid, this.name);

        this.npc.spawn(player.getLocation());
        this.npc.setProtected(false);

        Player npcPlayer = (Player) this.npc.getEntity();

        npcPlayer.getInventory().setContents(player.getInventory().getContents());
        npcPlayer.getInventory().setArmorContents(player.getInventory().getArmorContents());
        npcPlayer.setTotalExperience(player.getTotalExperience());

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public NPC getNpc() {
        return this.npc;
    }

    public long getDeathBanTime() {
        return this.deathBanTime;
    }

    public BukkitTask getBukkitTask() {
        return this.bukkitTask;
    }

    public void removeNPC() {
        this.npc.despawn();
        Main.getInstance().getCombatLogHandler().getNpcRegistry().deregister(this.npc);
    }

    public void updateTimer() {
        this.i = 30;
    }

    public void run() {
        if (this.i > 0)
            this.i -= 1;
        else
            cancel();
    }

    public void cancel() {
        removeNPC();
        super.cancel();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Main.getInstance().getCombatLogHandler().getNpcRegistry().isNPC(event.getEntity())) {
            NPC npc = Main.getInstance().getCombatLogHandler().getNpcRegistry().getAsNPC(event.getEntity());
            if (npc.equals(getNpc())) {
                Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(getUuid());
                Main.getInstance().getCombatLogHandler().addKilled(getUuid());

                Player killer = event.getEntity().getKiller();
                profile.setDeaths(profile.getDeaths() + 1);

                Faction faction = HCFAPI.getFactionManager().findByUser(getUuid());
                if (faction != null) {
                    double oldDTR = faction.getDTR();
                    faction.setDTR(oldDTR - 1.0);
                    if (faction.isRaidable().get())
                        faction.getDTRFreeze().set(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(90L));

                    Main.getInstance().getDtrHandler().setCooldown(faction);
                    faction.getDTRFreeze().set(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60L));
                    for (UUID uuid : faction.getAllMembers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null || !player.isOnline())
                            continue;

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMember death: &4" + event.getEntity().getName()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDTR change: &4" + oldDTR + " &e-> &4" + faction.getDTR()));
                    }
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7(Combat-Logger)&c" + getName() + "&4[" +
                        profile.getKills() +
                        "]&7 was slain by &c" +
                        killer.getName() +
                        "&4[" +
                        HCFAPI.getHCFManager().findProfileByUniqueId(getUuid()).getKills() +
                        "]&7 using &c" + getItemName(killer.getItemInHand()) + "&7."));
                cancel();
            }
        }
    }

    private String getItemName(final ItemStack i) {
        if (i.getItemMeta().hasDisplayName())
            return ChatColor.stripColor(i.getItemMeta().getDisplayName());

        return WordUtils.capitalizeFully(i.getType().name().replace('_', ' '));
    }
}
