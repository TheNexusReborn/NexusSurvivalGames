package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class GameSubCommand extends SubCommand<SurvivalGames> {
    public GameSubCommand(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, Rank minRank, String... aliases) {
        super(plugin, parent, 1, name, description, minRank, aliases);
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
        
        return handle(player, sgPlayer, game, args, flagResults);
    }
    
    protected abstract boolean handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults);
}
