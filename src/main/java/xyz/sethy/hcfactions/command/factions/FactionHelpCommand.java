package xyz.sethy.hcfactions.command.factions;

import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.command.SubCommand;

import java.util.Collections;

public class FactionHelpCommand extends SubCommand {
    public FactionHelpCommand() {
        super("help", Collections.singletonList("h"), true);
    }

    public void execute(Profile sender, String[] args) {
        if (args.length == 0) {
            sendDefaultFirstPage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "1": {
                sendDefaultFirstPage(sender);
                break;
            }
            case "2": {
                sender.sendMessage("&e&m-----------------------------------------------------");
                sender.sendMessage("&eFaction Help &fPage (#2/3)");
                sender.sendMessage(" &e/f withdraw &fWithdraw money from your faction.");
                sender.sendMessage(" &e/f sethome &fSet your faction's home.");
                sender.sendMessage(" &e/f home &fTeleport to your faction's home.");
                sender.sendMessage(" &e/f map &fShow all faction claims within 30 blocks.");
                sender.sendMessage(" &e/f list &fShow all other online factions.");
                sender.sendMessage(" &e/f show &fView a faction's information.");
                sender.sendMessage(" &e/f stuck &fEscape an enemy claim.");
                sender.sendMessage(" ");
                sender.sendMessage("&eTo view additional help pages, type &f/f help page#");
                sender.sendMessage("&e&m-----------------------------------------------------");
                break;
            }
            case "3": {
                sender.sendMessage("&e&m-----------------------------------------------------");
                sender.sendMessage("&eFaction Help &fPage (#3/3)");
                sender.sendMessage(" &e/f promote &fPromote a faction member to a captain.");
                sender.sendMessage(" &e/f demote &fDemote a faction captain to a member.");
                sender.sendMessage(" &e/f uninvite &fUninvite an invited player.");
                sender.sendMessage(" &e/f announce &fSet your faction's announcement.");
                sender.sendMessage(" ");
                sender.sendMessage("&eTo view additional help pages, type &f/f help page#");
                sender.sendMessage("&e&m-----------------------------------------------------");
                break;
            }
            default: {
                sendDefaultFirstPage(sender);
            }
        }
    }

    private void sendDefaultFirstPage(Profile sender) {
        sender.sendMessage("&e&m-----------------------------------------------------");
        sender.sendMessage("&eFaction Help &fPage (#1/3)");
        sender.sendMessage(" &e/f create &fCreate a new faction.");
        sender.sendMessage(" &e/f invite &fInvite a player to your faction.");
        sender.sendMessage(" &e/f join &fJoin a faction.");
        sender.sendMessage(" &e/f leave &fLeave the faction you are currently in.");
        sender.sendMessage(" &e/f kick &fKick a player from your faction.");
        sender.sendMessage(" &e/f claim &fObtain a claim wand to claim for your faction.");
        sender.sendMessage(" &e/f unclaim &fUnclaim your faction land.");
        sender.sendMessage(" &e/f deposit &fDeposit money into your faction.");
        sender.sendMessage(" ");
        sender.sendMessage("&eTo view additional help pages, type &f/f help page#");
        sender.sendMessage("&e&m-----------------------------------------------------");
    }
}
