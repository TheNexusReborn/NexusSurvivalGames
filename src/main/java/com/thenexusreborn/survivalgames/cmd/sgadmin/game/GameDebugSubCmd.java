package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.command.flags.type.PresenceFlag;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.entity.Player;

public class GameDebugSubCmd extends GameSubCommand {
    
    private static final PresenceFlag GLOBAL = new PresenceFlag("g", "Global");
    
    public GameDebugSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "debug", "Toggles the debug mode", Rank.ADMIN);
        this.cmdFlags.addFlag(GLOBAL);
    }
    
    @Override
    protected boolean handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (game.isDebugMode()) {
            game.disableDebug();
            game.sendMessage(MsgType.INFO.format("%v %v debug mode.", sgPlayer.getTrueColoredName(), "&cdisabled"));
        } else {
            game.enableDebug();
            game.sendMessage(MsgType.INFO.format("%v %v debug mode.", sgPlayer.getTrueColoredName(), "&aenabled"));
        }
        
        if (flagResults.isPresent(GLOBAL)) {
            plugin.setSgGlobalDebug(game.isDebugMode());
        }
        
        return true;
    }
}
