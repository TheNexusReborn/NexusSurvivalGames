package com.thenexusreborn.survivalgames.cmd.sgadmin.game.player;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.cmdflags.type.PresenceFlag;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.menu.MutateGui;
import com.thenexusreborn.survivalgames.menu.manage.ManageMutateMenu;
import com.thenexusreborn.survivalgames.mutations.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GameMutatePlayerSubCmd extends GamePlayerSubCmd {
    
    private static final PresenceFlag BYPASS_TIMER = new PresenceFlag("bt", "Bypass Timer");
    
    public GameMutatePlayerSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "mutate", "", Rank.ADMIN, "m");
        this.cmdFlags.addFlag(BYPASS_TIMER);
    }
    
    @Override
    protected boolean handle(Player sender, SGPlayer senderPlayer, Game game, GamePlayer target, String[] args, FlagResult flagResults) {
        // /sg game player mutate|m <player> <type|random|select|gui> [target]
        // The select option needs some backend reworks for mutations to work properly
        // The random option will not be available yet
        
        if (target.getTeam() != GameTeam.SPECTATORS) {
            sender.sendMessage(MsgType.WARN.format("%v is not a spectator.", target.getName()));
            return true;
        }
        
        MutationBuilder mutationBuilder = new MutationBuilder(target);
        
        if (!(args.length > 1)) {
            MsgType.WARN.format("Not enough arguments");
            return true;
        }
        
        
        if (args[1].equalsIgnoreCase("gui")) {
            ManageMutateMenu menu = new ManageMutateMenu(plugin, senderPlayer, game, mutationBuilder);
            plugin.getGuiManager().openGUI(menu, sender);
            return true;
        }
        
        if (args[1].equalsIgnoreCase("select")) {
            MutateGui mutateGui = new MutateGui(plugin, mutationBuilder);
            plugin.getGuiManager().openGUI(mutateGui, sender);
            return true;
        }
        
        if (args[1].equalsIgnoreCase("random")) {
            List<IMutationType> types = List.of(StandardMutations.values());
            mutationBuilder.setType(types.get(new Random().nextInt(types.size())));
        } else {
            mutationBuilder.setType(StandardMutations.getType(args[1]));
            
            if (mutationBuilder.getType() == null) {
                sender.sendMessage(MsgType.WARN.format("Invalid mutation type %v.", args[1]));
                return true;
            }
        }
        
        if (args.length > 2) {
            mutationBuilder.setTarget(game.getPlayer(args[2]));
            
            if (mutationBuilder.getTarget() == null) {
                MsgType.WARN.send(sender, "%v is not in the game."); 
                return true;
            }
        } else {
            if (mutationBuilder.getTarget() == null) {
                sender.sendMessage(MsgType.WARN.format("%v does not have a valid player killer.", target.getName()));
                return true;
            }
        }
        
        if (mutationBuilder.getTarget() == null) {
            sender.sendMessage(MsgType.SEVERE.format("You shouldn't see this message. Invalid Target, report as a bug."));
            sender.sendMessage(MsgType.SEVERE.format("This message comes up as a result of an unhandled check."));
            return true;
        }
        
        boolean bypassTimer = flagResults.isPresent(BYPASS_TIMER);
        
        game.mutatePlayer(senderPlayer, target, mutationBuilder.getType(), mutationBuilder.getTarget(), bypassTimer);
        return true;
    }
}
