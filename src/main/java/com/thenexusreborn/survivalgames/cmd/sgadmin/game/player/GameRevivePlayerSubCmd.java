package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.game.death.DeathInfo;
import com.thenexusreborn.survivalgames.loot.TableParseResult;
import org.bukkit.entity.Player;

public class GameRevivePlayerSubCmd extends GamePlayerSubCmd {
    public GameRevivePlayerSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "revive", "", Rank.ADMIN, "rv");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults) {
        if (target.getTeam() != GameTeam.SPECTATORS) {
            sender.sendMessage(MsgType.WARN.format("%v is not a spectator.", target.getName()));
            return true;
        }
        
        if (!target.isSpectatorByDeath()) {
            sender.sendMessage(MsgType.WARN.format("%v did not die in this game.", target.getName()));
            return true;
        }
        
        DeathInfo mostRecentDeath = target.getMostRecentDeath();
        
        if (mostRecentDeath == null) {
            sender.sendMessage(MsgType.WARN.format("%v does not have a most recent death.", target.getName()));
            return true;
        }
        
        TableParseResult parseResult;
        
        if (args.length > 1) {
            parseResult = plugin.getLootManager().parseTable(sender, args[1]);
            if (parseResult.invalidFormat() || parseResult.invalidNumber() || parseResult.invalidTable()) {
                return true;
            }
        } else {
            parseResult = TableParseResult.EMPTY;
        }
        
        game.revivePlayer(senderPlayer, target, parseResult.getLootTable(), parseResult.getAmount());
        return true;
    }
}
