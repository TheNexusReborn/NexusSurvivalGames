package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starcore.utils.actor.Actor;
import com.thenexusreborn.survivalgames.lobby.Lobby;

public class LobbyChatRoom extends ChatRoom {
    private Lobby lobby;
    
    public LobbyChatRoom(Lobby lobby) {
        super(lobby.getPlugin(), Actor.of(lobby.getPlugin()), "room-lobby-" + lobby.getServer().getName().toLowerCase().replace(" ", "_"));
        this.lobby = lobby;
        
        senderFormat.set("&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexuscore_displayname%&8: %nexuscore_chatcolor%{message}");
        systemFormat.set("{message}");
    }
}
