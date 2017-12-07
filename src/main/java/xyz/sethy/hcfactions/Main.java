package xyz.sethy.hcfactions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Location;
import xyz.sethy.hcfactions.api.impl.CoreHCFManager;
import xyz.sethy.hcfactions.command.coords.CoordsCommand;
import xyz.sethy.hcfactions.command.economy.BalanceCommand;
import xyz.sethy.hcfactions.command.economy.PayCommand;
import xyz.sethy.hcfactions.command.factions.*;
import xyz.sethy.hcfactions.command.factions.staff.FactionSetDTRCommand;
import xyz.sethy.hcfactions.command.factions.staff.FactionSetTypeCommand;
import xyz.sethy.hcfactions.command.koth.KothCreateCommand;
import xyz.sethy.hcfactions.command.koth.KothHelpCommand;
import xyz.sethy.hcfactions.command.koth.KothStartCommand;
import xyz.sethy.hcfactions.command.lives.*;
import xyz.sethy.hcfactions.command.lives.staff.LivesReviveCommand;
import xyz.sethy.hcfactions.command.lives.staff.LivesSetCommand;
import xyz.sethy.hcfactions.command.pvp.LogoutCommand;
import xyz.sethy.hcfactions.command.pvp.PvPEnableCommand;
import xyz.sethy.hcfactions.command.pvp.PvPTimeCommand;
import xyz.sethy.hcfactions.command.sotw.SotwEndCommand;
import xyz.sethy.hcfactions.command.sotw.SotwHelpCommand;
import xyz.sethy.hcfactions.command.sotw.SotwStartCommand;
import xyz.sethy.hcfactions.goose.GooseTicker;
import xyz.sethy.hcfactions.handler.*;
import xyz.sethy.hcfactions.listener.*;
import xyz.sethy.hcfactions.task.GlassWallTask;
import xyz.sethy.hcfactions.task.TPSTask;
import xyz.sethy.hcfactions.timer.TimerHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {
    private static Main instance;
    private AtomicBoolean kitmap;
    private TimerHandler timerHandler;
    private VisualClaimHandler visualClaimHandler;
    private ClaimHandler claimHandler;
    private ItemHandler itemHandler;
    private KothHandler kothHandler;
    private GooseHandler gooseHandler;
    private PvPClassHandler pvPClassHandler;
    private CombatLogHandler combatLogHandler;
    private DTRHandler dtrHandler;
    private DeathbanHandler deathbanHandler;
    private CommandHandler factionCommandHander;
    private CommandHandler pvpCommandHandler;
    private CommandHandler livesCommandHandler;
    private CommandHandler sotwCommandHandler;
    private CommandHandler kothCommandHandler;
    private org.bukkit.Location spawn;
    private org.bukkit.Location endExit;
    private org.bukkit.Location endEnterance;
    private TPSTask tpsTask;

    public synchronized static Main getInstance() {
        return instance;
    }

    private synchronized static void setInstance(Main newInstance) {
        instance = newInstance;
    }

    @Override
    public void onEnable() {
        setInstance(this);
        this.kitmap = new AtomicBoolean(false);
        HCFAPI.setHCFManager(new CoreHCFManager(), this);

        this.factionCommandHander = new CommandHandler("faction", "Factions Command", "/faction [subCommand]",
                Arrays.asList("factions", "f", "fac"));
        this.pvpCommandHandler = new CommandHandler("pvp", "PvP Command", "/pvp [subCommand]",
                Arrays.asList("p", "pvptime"));
        this.livesCommandHandler = new CommandHandler("lives", "Lives Command", "/lives [subCommand]",
                Collections.singletonList("l"));
        this.sotwCommandHandler = new CommandHandler("sotw", "SOTW command", "/sotw [subCommand]", new LinkedList<>());
        this.kothCommandHandler = new CommandHandler("koth", "KoTH command", "/koth [subCommand]", new LinkedList<>());
        this.timerHandler = new TimerHandler();
        this.visualClaimHandler = new VisualClaimHandler();
        this.claimHandler = new ClaimHandler();
        this.itemHandler = new ItemHandler();
        this.kothHandler = new KothHandler();
        this.gooseHandler = new GooseHandler();
        this.pvPClassHandler = new PvPClassHandler();
        this.combatLogHandler = new CombatLogHandler();
        this.dtrHandler = new DTRHandler();
        this.deathbanHandler = new DeathbanHandler();
        this.tpsTask = new TPSTask();
        this.spawn = new org.bukkit.Location(Bukkit.getWorld("world"), 0, 0, 0);
        this.endEnterance = new org.bukkit.Location(Bukkit.getWorld("world_the_end"), 0, 0, 0);
        this.endExit = new org.bukkit.Location(Bukkit.getWorld("world"), 0, 0, 0);

        setupFactionSubCommands();
        setupPvPSubCommands();
        setupLivesSubCommands();
        setupSotwSubCommands();
        setupKothCommands();

        setupListeners();

        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("logout").setExecutor(new LogoutCommand());
        getCommand("playtime").setExecutor(new PlayTimeCommand());
        getCommand("coords").setExecutor(new CoordsCommand());

//        ProtocolLibrary.getProtocolManager().addPacketListener(new AnvilSoundPacket());

        new TPSTask().runTaskTimer(this, 0L, 1L);
        new GooseTicker().runTaskTimerAsynchronously(this, 1L, 1L);
        new GlassWallTask().runTaskTimer(this, 10L, 10L);
//        new TabUpdateTask().runTaskTimer(this, 10L, 10L);
    }

    @Override
    public void onDisable() {
        for (Faction faction : HCFAPI.getHCFManager().findAll())
            HCFAPI.getRedisFactionDAO().update(faction);
    }

    private void setupFactionSubCommands() {
        this.factionCommandHander.setHelpPage(new FactionHelpCommand());
        this.factionCommandHander.addSubCommand("ally", new FactionAllyCommand());
        this.factionCommandHander.addSubCommand("chat", new FactionChatCommand());
        this.factionCommandHander.addSubCommand("claim", new FactionClaimCommand());
        this.factionCommandHander.addSubCommand("create", new FactionCreateCommand());
        this.factionCommandHander.addSubCommand("deinvite", new FactionDeinviteCommand());
        this.factionCommandHander.addSubCommand("deposit", new FactionDepositCommand());
        this.factionCommandHander.addSubCommand("disband", new FactionDisbandCommand());
        this.factionCommandHander.addSubCommand("help", new FactionHelpCommand());
        this.factionCommandHander.addSubCommand("home", new FactionHomeCommand());
        this.factionCommandHander.addSubCommand("invite", new FactionInviteCommand());
        this.factionCommandHander.addSubCommand("join", new FactionJoinCommand());
        this.factionCommandHander.addSubCommand("kick", new FactionKickCommand());
        this.factionCommandHander.addSubCommand("leader", new FactionLeaderCommand());
        this.factionCommandHander.addSubCommand("leave", new FactionLeaveCommand());
        this.factionCommandHander.addSubCommand("list", new FactionListCommand());
        this.factionCommandHander.addSubCommand("map", new FactionMapCommand());
        this.factionCommandHander.addSubCommand("promote", new FactionPromoteCommand());
        this.factionCommandHander.addSubCommand("rename", new FactionRenameCommand());
        this.factionCommandHander.addSubCommand("sethome", new FactionSetHomeCommand());
        this.factionCommandHander.addSubCommand("show", new FactionShowCommand());
        this.factionCommandHander.addSubCommand("stuck", new FactionStuckCommand());
        this.factionCommandHander.addSubCommand("unclaim", new FactionUnclaimCommand());
        this.factionCommandHander.addSubCommand("withdraw", new FactionWithdrawCommand());
        this.factionCommandHander.addSubCommand("setdtr", new FactionSetDTRCommand());
        this.factionCommandHander.addSubCommand("settype", new FactionSetTypeCommand());

        getCommand("factions").setExecutor(this.factionCommandHander);
    }

    private void setupPvPSubCommands() {
        this.pvpCommandHandler.setHelpPage(new PvPTimeCommand());
        this.pvpCommandHandler.addSubCommand("enable", new PvPEnableCommand());
        this.pvpCommandHandler.addSubCommand("time", new PvPTimeCommand());

        getCommand("pvp").setExecutor(this.pvpCommandHandler);
    }

    private void setupLivesSubCommands() {
        this.livesCommandHandler.setHelpPage(new LivesCheckCommand());
        this.livesCommandHandler.addSubCommand("check", new LivesCheckCommand());
        this.livesCommandHandler.addSubCommand("checkdeathban", new LivesCheckDeathbanCommand());
        this.livesCommandHandler.addSubCommand("help", new LivesHelpCommand());
        this.livesCommandHandler.addSubCommand("send", new LivesSendCommand());

        this.livesCommandHandler.addSubCommand("revive", new LivesReviveCommand());
        this.livesCommandHandler.addSubCommand("set", new LivesSetCommand());

        getCommand("lives").setExecutor(this.livesCommandHandler);
    }

    private void setupSotwSubCommands() {
        this.sotwCommandHandler.setHelpPage(new SotwHelpCommand());
        this.sotwCommandHandler.addSubCommand("end", new SotwEndCommand());
        this.sotwCommandHandler.addSubCommand("help", new SotwHelpCommand());
        this.sotwCommandHandler.addSubCommand("start", new SotwStartCommand());

        getCommand("sotw").setExecutor(this.sotwCommandHandler);
    }

    private void setupKothCommands() {
        this.kothCommandHandler.setHelpPage(new KothHelpCommand());
        this.kothCommandHandler.addSubCommand("help", new KothHelpCommand());
        this.kothCommandHandler.addSubCommand("create", new KothCreateCommand());
        this.kothCommandHandler.addSubCommand("start", new KothStartCommand());

        getCommand("koth").setExecutor(this.kothCommandHandler);
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new AsyncChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ASyncPreLoginEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(new FastTilesListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldSwitchListener(), this);

        Bukkit.getPluginManager().registerEvents(this.gooseHandler, this);
    }

    public TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public ClaimHandler getClaimHandler() {
        return claimHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    public KothHandler getKothHandler() {
        return kothHandler;
    }

    public GooseHandler getGooseHandler() {
        return gooseHandler;
    }

    public PvPClassHandler getPvPClassHandler() {
        return pvPClassHandler;
    }

    public CombatLogHandler getCombatLogHandler() {
        return combatLogHandler;
    }

    public DTRHandler getDtrHandler() {
        return dtrHandler;
    }

    public DeathbanHandler getDeathbanHandler() {
        return deathbanHandler;
    }

    public TPSTask getTpsTask() {
        return tpsTask;
    }

    public AtomicBoolean getKitmap() {
        return kitmap;
    }

    public VisualClaimHandler getVisualClaimHandler() {
        return visualClaimHandler;
    }

    public org.bukkit.Location getSpawn() {
        return spawn;
    }

    public org.bukkit.Location getEndExit() {
        return endExit;
    }

    public org.bukkit.Location getEndEnterance() {
        return endEnterance;
    }
}
