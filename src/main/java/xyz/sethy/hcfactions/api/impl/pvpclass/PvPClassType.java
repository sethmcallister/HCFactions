package xyz.sethy.hcfactions.api.impl.pvpclass;

public enum PvPClassType {
    BARD("&6Bard"),
    ARCHER("&aArcher"),
    MINER("&7Miner");

    private String scoreboardText;

    PvPClassType(String scoreboardText) {
        this.scoreboardText = scoreboardText;
    }

    public String getScoreboardText() {
        return scoreboardText;
    }
}
