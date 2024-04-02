package com.thenexusreborn.survivalgames.threads.lobby;

import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.lobby.Lobby;
import com.thenexusreborn.survivalgames.lobby.LobbyState;

public class VoteStartMsgThread extends NexusThread<SurvivalGames> {
    public VoteStartMsgThread(SurvivalGames plugin) {
        super(plugin, 2400L, true);
    }
    
    public void onRun() {
        for (Lobby lobby : plugin.getLobbies()) {
            if (lobby.getPlayers().isEmpty()) {
                continue;
            }

            if (lobby.getState() != LobbyState.WAITING) {
                continue;
            }

            if (lobby.getPlayers().size() < lobby.getLobbySettings().getMinPlayers()) {
                lobby.sendMessage("&6&l>> &e&lDid you know that you can use &f&l/votestart &e&lto start a game early?");
            }
        }
    }
}
