package xyz.sethy.hcfactions.command.factions;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;
import java.util.regex.Pattern;

public class FactionRenameCommand extends SubCommand {
    private final Pattern ALPHA_NUMERIC;

    public FactionRenameCommand() {
        super("rename", Collections.singletonList("tag"), true);
        this.ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    }

    @Override
    public void execute(Profile sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&cUsage: /faction rename <name>");
            return;
        }
        Faction faction = HCFAPI.getFactionManager().findByUser(sender.getUniqueId());
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
        String oldName = faction.getFactionName().get();
        faction.getFactionName().set(args[0]);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe faction &a" + oldName +
                "&e has been renamed to &a" + args[0] +
                "&e."));
    }
}
