package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starcore.actor.Actor;
import com.thenexusreborn.api.gamearchive.GameAction;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameState;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.entity.Player;

public class GameTeamChatroom extends ChatRoom {
    
    private Game game;
    
    public GameTeamChatroom(SurvivalGames plugin, Game game, GameTeam team) {
        super(plugin, Actor.of(plugin), "room-game-" + game.getServer().getName().toLowerCase().replace(" ", "_") + "-" + team.name().toLowerCase());
        this.game = game;
        senderFormat.set(team.getChatFormat());
        systemFormat.set("{message}");
    }

    @Override
    public void sendMessage(ChatContext chatContext) {
        if (!(chatContext.getSender() instanceof Player player)) {
            super.sendMessage(chatContext);
            return;
        }
        
        String message = chatContext.getMessage();
        
        if (game != null) {
            if (game.getState().ordinal() >= GameState.INGAME.ordinal() && game.getState().ordinal() <= GameState.DEATHMATCH.ordinal()) {
                if (game.getPlayer(player.getUniqueId()).getTeam() == GameTeam.SPECTATORS) {
                    game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "deadchat", player.getName() + ":" + message.replace("'", "''")));
                } else {
                    game.getGameInfo().getActions().add(new GameAction(System.currentTimeMillis(), "chat", player.getName() + ":" + message.replace("'", "''")));
                }
            }
        }
        
        super.sendMessage(chatContext);
    }
}
