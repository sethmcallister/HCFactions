package xyz.sethy.hcfactions.command.koth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.claims.type.VisualClaimType;
import xyz.sethy.hcfactions.command.SubCommand;
import xyz.sethy.hcfactions.impl.Koth;
import xyz.sethy.hcfactions.impl.claims.VisualClaim;

import java.util.LinkedList;
import java.util.List;

public class KothCreateCommand extends SubCommand {
    public KothCreateCommand() {
        super("create", new LinkedList<>(), true);
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (!sender.hasPermission("staff.hcf.admin")) {
            sender.sendMessage("&cYou do not have permission to execute this command.");
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /koth create <faction>");
            return;
        }
        List<Faction> factions = HCFAPI.getHCFManager().findByString(args[0]);
        if (factions.isEmpty()) {
            sender.sendMessage("&cNo factions have been found with the name or member of '" + args[0] + "'.");
            return;
        }
        Faction faction = factions.get(0);
        Koth koth = new Koth(faction.getFactionId(), faction.getFactionClaim());
        Main.getInstance().getKothHandler().addKoth(koth);
        Player player = Bukkit.getPlayer(sender.getUniqueId());

        if (Main.getInstance().getVisualClaimHandler().getCurrentMaps().containsKey(player.getUniqueId())) {
            VisualClaim claim = Main.getInstance().getVisualClaimHandler().getCurrentMaps().get(player.getUniqueId());
            if (claim == null)
                return;

            claim.cancel(true);
            return;
        }

        VisualClaim visualClaim = new VisualClaim(player, VisualClaimType.CREATE, true);
        Main.getInstance().getVisualClaimHandler().getCurrentMaps().put(player.getUniqueId(), visualClaim);
        player.getInventory().addItem(Main.getInstance().getItemHandler().getClaimWand());

    }
}
