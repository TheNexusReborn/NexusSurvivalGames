package com.thenexusreborn.survivalgames.chat;

import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starchat.rooms.ChatRoom;
import com.stardevllc.starcore.api.actors.Actor;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GameTeam;
import com.thenexusreborn.survivalgames.gamelog.GameChatAction;
import org.bukkit.entity.Player;

public class GameTeamChatroom extends ChatRoom {

    private Game game;

    public GameTeamChatroom(SurvivalGames plugin, Game game, GameTeam team) {
        super(plugin, Actor.of(plugin), "room-game-" + game.getServer().getName().toLowerCase().replace(" ", "_") + "-" + team.name().toLowerCase());
        this.game = game;
        senderFormat.set(team.getChatFormat());
        systemFormat.set("{message}");
        useColorPermissions.set(true);
    }

    @Override
    public void sendMessage(ChatContext chatContext) {
        if (!(chatContext.getSender() instanceof Player player)) {
            super.sendMessage(chatContext);
            return;
        }

        String message = chatContext.getMessage();

        if (game != null) {
            game.getGameInfo().getActions().add(new GameChatAction(this, player.getName(), message.replace("'", "''")));
        }

        super.sendMessage(chatContext);
    }
}
