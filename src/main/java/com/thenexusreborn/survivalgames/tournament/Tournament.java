package com.thenexusreborn.survivalgames.tournament;

import com.thenexusreborn.survivalgames.mutations.IMutationType;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import com.thenexusreborn.survivalgames.settings.LobbySettings;

import java.util.*;

public class Tournament {
    protected String name;
    protected UUID host;
    protected TournamentSettings tournamentSettings;
    protected LobbySettings lobbySettings;
    protected GameSettings gameSettings;
    protected Set<IMutationType> disabledMutations = new HashSet<>(); //Exclusive to enabledMutations
    protected Set<IMutationType> enabledMutations = new HashSet<>(); //Exclusive to disabledMutations
}