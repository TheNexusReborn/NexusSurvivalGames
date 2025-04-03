package com.thenexusreborn.survivalgames.cmd.sgadmin.timer.modify;

import com.stardevllc.clock.clocks.Timer;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.time.TimeFormat;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.gamearchive.GameAction;
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

public abstract class ModifySubCommand extends SubCommand<SurvivalGames> {
    
    public static final TimeFormat timeFormat = new TimeFormat("%*#0h%%*#0m%%*#0s%");
    public static final TimeParser timeParser = new TimeParser();
    
    public ModifySubCommand(SurvivalGames plugin, ICommand<SurvivalGames> parent, String name, String description, String... aliases) {
        super(plugin, parent, 2, name, description, Rank.ADMIN, aliases);
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
        
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a value.");
            return true;
        }
        
        long timeValue = timeParser.parseTime(args[0]);
        long oldValue = timer.getTime();
        
        boolean handle = handle(player, timer, timerType, timeValue, oldValue);
        long newValue = timer.getTime();
        
        if (timerType.equalsIgnoreCase("game")) {
            game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "timermodification").addValueData("actor", sender.getName()).addValueData("oldvalue", oldValue).addValueData("newvalue", newValue));
        }
        return handle;
    }
    
    public abstract boolean handle(Player sender, Timer timer, String timerType, long timeValue, long oldValue);
}
