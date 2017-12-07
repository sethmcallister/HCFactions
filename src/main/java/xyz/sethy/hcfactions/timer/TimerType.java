package xyz.sethy.hcfactions.timer;

public enum TimerType {
    COMBAT_TAG("&c&lSpawn Tag"),
    ENDERPEARL("&e&lEnderpearl"),
    PVP_TIMER("&aPvP Timer"),
    TELEPORT("&9Home"),
    ARCHER_TAG("&6&lArcher Mark"),
    ARCHER_COOLDOWN("&4Speed"),
    F_STUCK("&9Stuck"),
    LOGOUT("&4Logout"),
    KOTH("&6%koth_name%"),
    CLASS_WARM_UP("&6Class Warm-up");

    private String score;

    TimerType(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }
}