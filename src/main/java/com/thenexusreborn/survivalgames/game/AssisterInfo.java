package com.thenexusreborn.survivalgames.game;

public class AssisterInfo {
    private GamePlayer gamePlayer;
    private double credits, xp;
    private int score;
    
    public AssisterInfo(Game game, GamePlayer player, int scoreGain) {
        this.gamePlayer = player;
        if (game.getSettings().assists.credits.enabled) {
            credits = game.getSettings().assists.credits.amount;
        }
        
        if (game.getSettings().assists.xp.enabled) {
            xp = game.getSettings().assists.xp.amount;
        }
        
        if (game.getSettings().assists.score.enabled) {
            if (game.getSettings().assists.score.relative) {
                score = scoreGain / game.getSettings().assists.score.amount;
            } else {
                score = game.getSettings().assists.score.amount;
            }
        }
    }
    
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
    
    public double getCredits() {
        return credits;
    }
    
    public double getXp() {
        return xp;
    }
}