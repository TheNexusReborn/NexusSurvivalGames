package com.thenexusreborn.survivalgames.game;

public class AssisterInfo {
    private GamePlayer gamePlayer;
    private double credits, xp, nexites;
    
    public AssisterInfo(Game game, GamePlayer player) {
        this.gamePlayer = player;
        if (game.getSettings().isGiveCredits()) {
            credits = game.getSettings().getAssistCreditGain();
        }
        
        if (game.getSettings().isGiveXp()) {
            xp = game.getSettings().getAssistXPGain();
        }
        
        if (game.getSettings().isEarnNexites()) {
            nexites = game.getSettings().getAssistNexiteGain();
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
    
    public double getNexites() {
        return nexites;
    }
}
