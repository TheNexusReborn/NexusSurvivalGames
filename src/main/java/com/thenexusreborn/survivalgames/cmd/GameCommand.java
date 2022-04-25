package com.thenexusreborn.survivalgames.cmd;

import com.thenexusreborn.nexuscore.player.Rank;
import com.thenexusreborn.nexuscore.util.command.*;
import com.thenexusreborn.survivalgames.*;
import com.thenexusreborn.survivalgames.cmd.game.*;
import com.thenexusreborn.survivalgames.game.Game;

import java.util.*;

public class GameCommand extends NexusCommand {
    
    public GameCommand(SurvivalGames plugin) {
        super("game", "Modify the active game", Rank.ADMIN, true, false, new ArrayList<>());
        addSubCommand(new SetupSubCommand(plugin, this));
        addSubCommand(new AssignStartingTeamsSubCommand(plugin, this));
        addSubCommand(new TeleportPlayersSubCommand(plugin, this));
        addSubCommand(new StartCountdownSubCommand(plugin, this));
        addSubCommand(new StartGameSubCommand(plugin, this));
        addSubCommand(new TimerSubCommand(plugin, this));
        addSubCommand(new StartDeathmatchTimerSubCommand(plugin, this));
        addSubCommand(new TeleportDeathmatchSubCommand(plugin, this));
        addSubCommand(new StartDeathmatchCountdownSubCommand(plugin, this));
        addSubCommand(new StartDeathmatchSubCommand(plugin, this));
        addSubCommand(new EndGameSubCommand(plugin, this));
        addSubCommand(new NextGameSubCommand(plugin, this));
        addSubCommand(new RestockchestsSubCommand(plugin, this));
        addSubCommand(new SubCommand(this, "automatic", "Set the game progression to automatic", getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                Game.setMode(Mode.AUTOMATIC);
                actor.sendMessage("&eSet the game progression to automatic");
            }
        });
    
        addSubCommand(new SubCommand(this, "manual", "Set the game progression to manual", getMinRank()) {
            @Override
            public void handleCommand(NexusCommand nexusCommand, CommandActor actor, String[] previousArgs, String label, String[] args) {
                Game.setMode(Mode.MANUAL);
                actor.sendMessage("&eSet the game progression to manual");
            }
        });
    }
}
