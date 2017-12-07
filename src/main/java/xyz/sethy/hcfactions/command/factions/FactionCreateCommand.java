package xyz.sethy.hcfactions.command.factions;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.CoreHCFManager;
import xyz.sethy.hcfactions.api.impl.HCFaction;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class FactionCreateCommand extends SubCommand {
    private final Pattern ALPHA_NUMERIC;

    public FactionCreateCommand() {
        super("create", new LinkedList<>(), true);
        this.ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    }

    @Override
    public void execute(Profile sender, String[] args) {
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
        if (faction != null) {
            sender.sendMessage("&cYou are already in a faction.");
            return;
        }
        if (args.length < 1) {
            sender.sendMessage("&cPlease specify a faction name");
            return;
        }
        if (ALPHA_NUMERIC.matcher(args[0]).find()) {
            sender.sendMessage("&cFaction names must be alphanumeric.");
            return;
        }
        if (StringUtils.length(args[0]) > 16) {
            sender.sendMessage("&cYour faction name cannot be longer than 16 characters.");
            return;
        }
        if (StringUtils.length(args[0]) < 3) {
            sender.sendMessage("&cYour faction name must be at-least 3 characters.");
            return;
        }
        if (!HCFAPI.getFactionManager().findByString(args[0]).isEmpty()) {
            sender.sendMessage("&cA faction with the name '" + args[0] + "' already exists.");
            return;
        }
        faction = new HCFaction(args[0], sender.getUniqueId());
        ((CoreHCFManager) HCFAPI.getFactionManager()).addFaction(faction);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe faction &f" +
                faction.getFactionName().get() +
                "&e has been created by &f" +
                sender.getName() + "&e."));

        sender.setFactionId(faction.getFactionId());
    }
}
