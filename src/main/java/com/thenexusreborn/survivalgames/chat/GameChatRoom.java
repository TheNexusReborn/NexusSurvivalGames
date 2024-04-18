package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starcore.actor.Actor;
import com.thenexusreborn.survivalgames.game.Game;

public class GameChatRoom extends ChatRoom {
    private Game game;
    
    public GameChatRoom(Game game) {
        super(Game.getPlugin(), Actor.of(Game.getPlugin()), "room-game-" + game.getServer().getName().toLowerCase().replace(" ", "_") + "-main");
        this.game = game;
        
        senderFormat.set("{message}");
        systemFormat.set("{message}");
    }
}
