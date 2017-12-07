package xyz.sethy.hcfactions.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.sethy.hcfactions.Main;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class WorldSwitchListener implements Listener {
    @EventHandler
    public void onWorldSwitch(final PlayerTeleportEvent event) {
        if(event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            event.getPlayer().teleport(Main.getInstance().getEndExit());
        } else if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL && event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
            event.getPlayer().teleport(Main.getInstance().getEndEnterance());
        }
    }
}
