package com.thenexusreborn.survivalgames.game.state;

import com.thenexusreborn.survivalgames.game.Game;

import java.util.LinkedList;

/**
 * For the new way of handling how the game progression is tracked
 */
public class GameState {
    private Game game;
    private GamePhase phase; //Current Phase
    private LinkedList<GamePhase> pastPhases = new LinkedList<>();
}
