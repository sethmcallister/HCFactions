package xyz.sethy.hcfactions.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.sethy.hcfactions.Main;

import java.util.LinkedList;
import java.util.List;

public class AnvilSoundPacket extends PacketAdapter {
    private static final List<Location> blockedSounds;

    static {
        blockedSounds = new LinkedList<>();
    }

    public AnvilSoundPacket() {
        super(Main.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_SOUND_EFFECT);
    }

    public static void addLocation(Location location) {
        blockedSounds.add(location);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // Item packets (id: 0x29)
        if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            int x = Integer.parseInt(event.getPacket().getStrings().read(2)) / 8;
            int y = Integer.parseInt(event.getPacket().getStrings().read(3)) / 8;
            int z = Integer.parseInt(event.getPacket().getStrings().read(4)) / 8;
            Location location = new Location(Bukkit.getWorld("world"), x, y, z);
            if (!containsLocation(location))
                return;

            blockedSounds.remove(location);
            event.setCancelled(true);
        }
    }

    private boolean containsLocation(Location location) {
        for (Location location1 : blockedSounds) {
            if (location.getBlockX() == location1.getBlockX() &&
                    location.getBlockY() == location1.getBlockY() &&
                    location.getBlockZ() == location1.getBlockY()) {
                return true;
            }
        }
        return false;
    }
}
