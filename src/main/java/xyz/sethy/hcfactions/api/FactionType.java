package xyz.sethy.hcfactions.api;

public enum FactionType {
    PLAYER("&e(&cDeathban&e)"),
    SAFE("&e(&aNon-Deathban&e)"),
    KOTH("&e(&6KoTH&e)"),
    CONQUEST("&e(&6Conquest&e)"),
    TEST("&e(&5Test&e)"),
    ROAD("&e(&6Road&e)");

    String deathban;

    FactionType(String deathban) {
        this.deathban = deathban;
    }

    public String getDeathban() {
        return this.deathban;
    }
}
