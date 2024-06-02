package com.thenexusreborn.survivalgames.gamelog;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.thenexusreborn.api.gamearchive.GameAction;

public class GameChatAction extends GameAction {
    public GameChatAction(ChatRoom chatRoom, String sender, String message) {
        super(System.currentTimeMillis(), "chat", "");
        addValueData("sender", sender);
        addValueData("message", message);
        addValueData("chatroom", chatRoom.getName());
    }
}
