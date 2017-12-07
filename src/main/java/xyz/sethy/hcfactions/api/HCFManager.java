package xyz.sethy.hcfactions.api;

import java.util.List;
import java.util.UUID;

public interface HCFManager {
    /**
     * Find the faction that has the UUID of @param
     *
     * @param uuid
     * @return
     * @see Faction
     */
    Faction findByUniqueId(UUID uuid);

    /**
     * Find a faction that contains the user @param
     *
     * @param user
     * @return
     * @see Faction
     */
    Faction findByUser(UUID user);

    /**
     * Get all factions the name @param or the player with the name @param
     *
     * @param string
     * @return
     * @see List
     */
    List<Faction> findByString(String string);

    /**
     * Get all factions the name @param or the player with the name @param
     *
     * @return
     * @see List
     */
    List<Faction> findAll();

    /**
     * Disband the @param faction
     *
     * @param faction
     * @return
     */
    void disbandFaction(Faction faction);

    /**
     * Find the Profile that has the UUID of @param
     *
     * @param uuid
     * @return
     * @see Profile
     */
    Profile findProfileByUniqueId(UUID uuid);

    /**
     * Find the Profile that has the Name of @param
     *
     * @param string
     * @return
     * @see Profile
     */
    Profile findProfileByString(String string);
}
