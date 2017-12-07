package xyz.sethy.hcfactions.api;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public interface Faction {

    UUID getFactionId();

    AtomicReference<String> getFactionName();

    List<UUID> getAllMembers();

    List<UUID> getMembers();

    List<UUID> getCaptains();

    List<UUID> getCoLeaders();

    List<UUID> getAllies();

    List<UUID> getInvites();

    List<UUID> getAllyRequests();

    AtomicReference<UUID> getLeader();

    Double getBalance();

    void setBalance(Double balance);

    Double getDTR();

    void setDTR(Double dtr);

    Double getMaxDTR();

    Location getHome();

    void setHome(Location location);

    AtomicLong getDTRFreeze();

    String getFactionInformation(UUID sender);

    Claim getFactionClaim();

    AtomicBoolean isRaidable();

    AtomicReference<FactionType> getFactionType();

    void setClaim(Claim claim);

    boolean isCaptainOrHigher(UUID uuid);
}
