package xyz.sethy.hcfactions.api;

import org.bukkit.plugin.Plugin;
import xyz.sethy.hcfactions.api.impl.dao.RedisFactionDAO;
import xyz.sethy.hcfactions.api.impl.dao.RedisProfileDAO;

public abstract class HCFAPI {
    private static final RedisFactionDAO redisFactionDAO = new RedisFactionDAO();
    private static final RedisProfileDAO redisProfileDAO = new RedisProfileDAO();
    private static HCFManager hcfManager;
    private static Plugin plugin;

    private HCFAPI() {
    }

    public static void setHCFManager(HCFManager coreFactionsManager, Plugin corePlugin) {
        hcfManager = coreFactionsManager;
        plugin = corePlugin;
    }

    @Deprecated
    public static HCFManager getFactionManager() {
        return hcfManager;
    }


    public static HCFManager getHCFManager() {
        return hcfManager;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static RedisFactionDAO getRedisFactionDAO() {
        return redisFactionDAO;
    }

    public static RedisProfileDAO getRedisProfileDAO() {
        return redisProfileDAO;
    }

    public static String getMapName() {
        return "1";
    }
}
