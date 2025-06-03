package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.control.ControlType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameControlSubCmd extends SubCommand<SurvivalGames> {
    
    private ControlType controlType;
    
    public GameControlSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, ControlType controlType) {
        super(plugin, parent, 1, controlType.name(), "", Rank.ADMIN, controlType.getAliases());
        this.controlType = controlType;
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        Game game = sgPlayer.getGame();
        
        if (game == null) {
            MsgType.WARN.send(sender, "You are not in a game.");
            return true;
        }
        
        if (game.getControlType() == this.controlType) {
            MsgType.WARN.send(sender, "The game is already in %v", this.controlType.name().toLowerCase());
            return true;
        }
        
        game.setControlType(controlType);
        game.sendMessage(MsgType.INFO.format("The game has been set to %v by %v", controlType.name().toLowerCase(), sgPlayer.getColoredName()));
        return true;
    }
}
