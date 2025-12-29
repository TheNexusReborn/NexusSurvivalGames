package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.loot.TableParseResult;
import org.bukkit.entity.Player;

public class GameAddPlayerSubCmd extends GamePlayerSubCmd {
    public GameAddPlayerSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "add", "", Rank.ADMIN, "a");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults) {
        if (target.getTeam() != GameTeam.SPECTATORS) {
            MsgType.WARN.send(sender, "%v is not a spectator.", target.getName());
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
        
        game.addAsTribute(senderPlayer, target, parseResult.getLootTable(), parseResult.getAmount());
        return true;
    }
}
