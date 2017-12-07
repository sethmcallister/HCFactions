package xyz.sethy.hcfactions.command;

import xyz.sethy.hcfactions.api.Profile;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SubCommand {
    private final AtomicReference<String> subCommand;
    private final List<String> aliases;
    private final AtomicBoolean playerOnly;

    public SubCommand(String subCommand, List<String> aliases, Boolean playerOnly) {
        this.subCommand = new AtomicReference<>(subCommand);
        this.aliases = aliases;
        this.playerOnly = new AtomicBoolean(playerOnly);
    }

    public AtomicReference<String> getSubCommand() {
        return subCommand;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public AtomicBoolean getPlayerOnly() {
        return playerOnly;
    }

    public abstract void execute(Profile sender, String[] args);
}
