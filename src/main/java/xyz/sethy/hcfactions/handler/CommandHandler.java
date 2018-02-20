package xyz.sethy.hcfactions.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.hcfactions.Main;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler implements CommandExecutor {
    private final Map<String, SubCommand> commands;
    private SubCommand defaultCommand;

    public CommandHandler(String command, String description, String usage, List<String> aliases) {
        this.commands = new ConcurrentHashMap<>();
        this.defaultCommand = null;
    }

    public void setHelpPage(SubCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public void addSubCommand(String command, SubCommand subCommand) {
        this.commands.put(command, subCommand);
    }

    private SubCommand getSubCommand(String subCommand) {
        for (Map.Entry<String, SubCommand> subCommandEntry : commands.entrySet()) {
            if (subCommandEntry.getValue().getSubCommand().get().equalsIgnoreCase(subCommand)) {
                for (String alias : subCommandEntry.getValue().getAliases()) {
                    if (alias.equalsIgnoreCase(subCommand)) {
                        return subCommandEntry.getValue();
                    }
                }
                return subCommandEntry.getValue();
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        Profile profile = HCFAPI.getHCFManager().findProfileByUniqueId(player.getUniqueId());
        if (args.length == 0) {
            defaultCommand.execute(profile, args);
            return true;
        }
        final SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand == null) {
            profile.sendMessage("&cNo sub-command with the name '" + args[0] + "' was found.");
            return true;
        }
        List<String> subArgs = new LinkedList<>();
        for (String args1 : args) {
            if (args1.equals(args[0]))
                continue;

            subArgs.add(args1);
        }
        String[] newargs = subArgs.toArray(new String[subArgs.size()]);
        subArgs.toArray(newargs);
        new BukkitRunnable() {
            @Override
            public void run() {
                subCommand.execute(profile, newargs);
            }
        }.runTaskAsynchronously(Main.getInstance());
        return true;
    }
}
