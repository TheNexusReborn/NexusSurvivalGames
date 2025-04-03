package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import org.bukkit.entity.Player;

public class GameRemovePlayerSubCmd extends GamePlayerSubCmd {
    public GameRemovePlayerSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "remove", "", Rank.ADMIN, "rm");
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults) {
        if (target.getTeam() == GameTeam.SPECTATORS) {
            sender.sendMessage(MsgType.WARN.format("%v is already a spectator.", target.getName()));
            return true;
        }
        
        game.removeFromGame(senderPlayer, target);
        return true;
    }
}
