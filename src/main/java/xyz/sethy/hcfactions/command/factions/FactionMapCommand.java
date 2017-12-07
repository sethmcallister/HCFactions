package xyz.sethy.hcfactions.command.factions;

import org.bukkit.Bukkit;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.claims.type.VisualClaimType;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.impl.claims.VisualClaim;

import java.util.LinkedList;

public class FactionMapCommand extends SubCommand {
    public FactionMapCommand() {
        super("map", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("&cUsage: /faction map");
            return;
        }
        VisualClaim visualClaim = new VisualClaim(Bukkit.getPlayer(sender.getUniqueId()), VisualClaimType.MAP, false);
        Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(sender.getUniqueId(), visualClaim);
    }
}
