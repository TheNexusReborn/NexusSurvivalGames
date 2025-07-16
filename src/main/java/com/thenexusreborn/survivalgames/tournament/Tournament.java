package com.thenexusreborn.survivalgames.tournament;

import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class Tournament {
    protected String name;
    protected String host;
    protected Consumer<LobbySettings> lobbySettings;
    protected Consumer<GameSettings> gameSettings;
    protected Consumer<Player> winnerPrizes;
}