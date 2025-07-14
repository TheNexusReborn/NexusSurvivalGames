package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starmclib.actors.Actor;
import com.thenexusreborn.survivalgames.lobby.Lobby;

public class LobbyChatRoom extends ChatRoom {
    public LobbyChatRoom(Lobby lobby) {
        super(lobby.getPlugin(), Actor.of(lobby.getPlugin()), "room-lobby-" + lobby.getServer().getName().toLowerCase().replace(" ", "_"));
        
        senderFormat.set("&8<&3%nexussg_score%&8> &8(&2&l%nexuscore_level%&8) &r%nexuscore_displayname%&8: %nexuscore_chatcolor%{message}");
        systemFormat.set("{message}");
        useColorPermissions.set(true);
    }
}
