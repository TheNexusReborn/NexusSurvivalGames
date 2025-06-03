package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class GamePlayerSubCmd extends SubCommand<SurvivalGames> {
    public GamePlayerSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, Rank minRank, String... aliases) {
        super(plugin, parent, 2, name, description, minRank, aliases);
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
        
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a player name");
            return true;
        }
        
        GamePlayer target = game.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MsgType.WARN.format("Unknown player %v", args[0]));
            return true;
        }
        
        return handle(player, sgPlayer, game, target, args, flagResults);
    }
    
    protected abstract boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults);
}
