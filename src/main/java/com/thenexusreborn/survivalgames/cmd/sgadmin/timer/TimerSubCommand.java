package com.thenexusreborn.survivalgames.cmd.sgadmin.timer;

import com.stardevllc.starlib.clock.clocks.Timer;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TimerSubCommand extends SubCommand<SurvivalGames> {
    public TimerSubCommand(SurvivalGames plugin, ICommand<SurvivalGames> parent, int index, String name, String description, String... aliases) {
        super(plugin, parent, index, name, description, Rank.ADMIN, aliases);
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        Game game = sgPlayer.getGame();
        Lobby lobby = sgPlayer.getLobby();
        
        
        Timer timer;
        String timerType;
        if (game != null) {
            timer = game.getTimer();
            timerType = "game";
        } else {
            timer = lobby.getTimer();
            timerType = "lobby";
        }
        
        if (timer == null) {
            sender.sendMessage(MsgType.WARN.format("The %v does not have an active timer. Nothing to control.", timerType));
            return true;
        }
        
        return handle(player, timer);
    }
    
    protected abstract boolean handle(Player sender, Timer timer);
}
