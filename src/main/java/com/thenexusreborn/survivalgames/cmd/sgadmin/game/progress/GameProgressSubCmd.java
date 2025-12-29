package com.thenexusreborn.survivalgames.cmd.sgadmin.game.progress;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.command.flags.type.PresenceFlag;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class GameProgressSubCmd extends SubCommand<SurvivalGames> {
    
    protected static final PresenceFlag PREVIOUS = new PresenceFlag("p", "Previous");
    
    public GameProgressSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, String... aliases) {
        super(plugin, parent, 1, name, description, Rank.ADMIN, aliases);
        this.cmdFlags.addFlag(PREVIOUS);
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
        
        if (game.getState() == Game.State.ERROR) {
            MsgType.WARN.send(sender, "The game is in error status");
            return true;
        }
        
        handle(player, sgPlayer, game, args, flagResults);
        return true;
    }
    
    protected abstract void handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults);
}
