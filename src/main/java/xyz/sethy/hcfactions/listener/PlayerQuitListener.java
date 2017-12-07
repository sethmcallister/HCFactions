package xyz.sethy.hcfactions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.sethy.hcfactions.Main;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(final PlayerQuitEvent event) {
        if (Main.getInstance().getPvPClassHandler().hasPvPClass(event.getPlayer())) {
            Main.getInstance().getPvPClassHandler().getPvPClassObj(event.getPlayer()).onUnEquip(event.getPlayer());
        }
    }
}
