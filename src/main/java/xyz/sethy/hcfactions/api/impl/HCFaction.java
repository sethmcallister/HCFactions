package xyz.sethy.hcfactions.api.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.api.*;
import xyz.sethy.hcfactions.api.impl.claims.HCClaim;
import xyz.sethy.hcfactions.api.impl.claims.HCLocation;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class HCFaction implements Faction {
    private UUID uuid;
    private AtomicReference<String> name;
    private List<UUID> allMembers;
    private List<UUID> members;
    private List<UUID> captains;
    private List<UUID> allies;
    private List<UUID> invites;
    private List<UUID> coleaders;
    private List<UUID> allyRequests;
    private AtomicReference<UUID> leader;
    private Double balance;
    private Double dtr;
    private Double maxDtr;
    private HCLocation home;
    private AtomicLong dtrFreeze;
    private HCClaim claim;
    private AtomicReference<FactionType> factionType;

    public HCFaction(final String name, final UUID leader) {
        this.uuid = UUID.randomUUID();
        this.name = new AtomicReference<>(name);
        this.allMembers = new LinkedList<>();
        this.allMembers.add(leader);

        this.members = new LinkedList<>();
        this.captains = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.invites = new LinkedList<>();
        this.coleaders = new LinkedList<>();
        this.allyRequests = new LinkedList<>();
        this.leader = new AtomicReference<>(leader);
        this.balance = 0d;
        this.dtr = 1.01;
        this.maxDtr = 1.01;
        this.dtrFreeze = new AtomicLong(0);
        this.home = null;
        this.claim = null;
        this.factionType = new AtomicReference<>(FactionType.PLAYER);
    }


    public UUID getFactionId() {
        return uuid;
    }

    public AtomicReference<String> getFactionName() {
        return name;
    }

    public List<UUID> getAllMembers() {
        return allMembers;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<UUID> getCaptains() {
        return captains;
    }

    @Override
    public List<UUID> getCoLeaders() {
        return coleaders;
    }

    public List<UUID> getAllies() {
        return allies;
    }

    @Override
    public List<UUID> getInvites() {
        return invites;
    }

    @Override
    public List<UUID> getAllyRequests() {
        return allyRequests;
    }

    public AtomicReference<UUID> getLeader() {
        return leader;
    }

    public Double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getDTR() {
        return dtr;
    }

    @Override
    public void setDTR(Double dtr) {
        this.dtr = dtr;
    }

    public Double getMaxDTR() {
        return maxDtr;
    }

    public Location getHome() {
        HCLocation home = this.home;
        if (home.getWorldId().get() == null)
            System.out.println("world id == null");
        else if (home.getX() == null)
            System.out.println("x == null");
        else if (home.getY() == null)
            System.out.println("y == null");
        else if (home.getZ() == null)
            System.out.println("z == null");
        return this.home;
    }

    @Override
    public void setHome(Location location) {
        this.home = (HCLocation) location;
    }

    public AtomicLong getDTRFreeze() {
        return dtrFreeze;
    }

    public String getFactionInformation(UUID sender) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&e&m-------------------------------------------------").append("\n");
        stringBuilder.append("&eFaction: ")
                .append(this.allMembers.contains(sender) ? "&a" + name.get() : "&c" + name.get())
                .append(" &eHome: &f")
                .append(home == null ? "None" : home.getX() + ", " + home.getZ()).append("\n");
        Player leader = Bukkit.getPlayer(this.leader.get());
        if (leader == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.leader.get());
            Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(offlinePlayer.getUniqueId());
            stringBuilder.append(" &eLeader: &7").append(offlinePlayer.getName()).append("&f[")
                    .append(profile.getKills())
                    .append("&f]").append("\n");
        } else {
            Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(leader.getUniqueId());
            stringBuilder.append(" &eLeader: &a").append(leader.getName()).append("&f[")
                    .append(profile.getKills())
                    .append("&f]").append("\n");
        }
        if (this.captains.size() > 0) {
            StringBuilder b1 = new StringBuilder();
            for (UUID uuid : captains) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    b1.append("&7")
                            .append(offlinePlayer.getName())
                            .append("&f[")
                            .append(profile.getKills())
                            .append("&f]");
                    continue;
                }
                Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                b1.append("&a")
                        .append(player.getName())
                        .append("&f[")
                        .append(profile.getKills())
                        .append("&f]");
            }
            if (b1.toString().trim().endsWith(",")) {
                b1.append(b1.substring(0, b1.length() - 1));
            }
            stringBuilder.append(" &eCaptains: ").append(b1.toString()).append("\n");
        }
        if (this.members.size() > 0) {
            StringBuilder b1 = new StringBuilder();
            for (UUID uuid : members) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                    b1.append("&7")
                            .append(offlinePlayer.getName())
                            .append("&f[")
                            .append(profile.getKills())
                            .append("&f]");
                    continue;
                }
                Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(uuid);
                b1.append("&a")
                        .append(player.getName())
                        .append("&f[")
                        .append(profile.getKills())
                        .append("&f]");
            }
            if (b1.toString().trim().endsWith(",")) {
                b1.append(b1.substring(0, b1.length() - 1));
            }
            stringBuilder.append(" &eMembers: ").append(b1.toString()).append("\n");
        }
        stringBuilder.append(" &eBalance: &f$").append(this.balance).append("\n");
        stringBuilder.append("&eDeaths Until Raidable: ")
                .append(dtr >= 0.0 ? "&a" : "&c")
                .append(dtrFreeze.get() >= 0L ? "" : "\u25B2")
                .append(dtr).append("\n");
        stringBuilder.append("&e&m-------------------------------------------------");
        return ChatColor.translateAlternateColorCodes('&', stringBuilder.toString());
    }

    public Claim getFactionClaim() {
        return claim;
    }

    @Override
    public AtomicBoolean isRaidable() {
        return new AtomicBoolean(this.dtr <= 0.0D);
    }

    @Override
    public AtomicReference<FactionType> getFactionType() {
        return factionType;
    }

    @Override
    public void setClaim(Claim claim) {
        this.claim = (HCClaim) claim;
    }

    @Override
    public boolean isCaptainOrHigher(UUID uuid) {
        return this.leader.get().equals(uuid) || this.captains.contains(uuid);

    }

    public BigDecimal getDTRIncrement() {
        int online = 0;
        for (UUID uuid : allMembers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                online++;
        }
        return this.getDTRIncrement(online);
    }

    public BigDecimal getDTRIncrement(final int playersOnline) {
        return BigDecimal.valueOf(this.dtr + 1);
    }
}
