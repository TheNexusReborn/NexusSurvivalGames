package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starmclib.actor.Actor;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameTeam;

public class GameTeamChatroom extends ChatRoom {

    public GameTeamChatroom(SurvivalGames plugin, Game game, GameTeam team) {
        super(plugin, "room-game-" + game.getLocalId() + "-" + team.name().toLowerCase(), Actor.getServerActor(), team.getChatFormat(), "{message}");
    }
}
