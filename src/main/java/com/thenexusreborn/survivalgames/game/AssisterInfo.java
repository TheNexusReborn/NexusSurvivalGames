package com.thenexusreborn.survivalgames.game;

public class AssisterInfo {
    private GamePlayer gamePlayer;
    private double credits, xp, nexites;
    
    public AssisterInfo(Game game, GamePlayer player) {
        if (game.getSettings().isGiveCredits()) {
            credits = game.getSettings().getAssistCreditGain();
        }
        
        if (game.getSettings().isGiveXp()) {
            xp = game.getSettings().getAssistXPGain();
        }
        
        if (game.getSettings().isEarnNexites()) {
            nexites = game.getSettings().getAssistNexiteGain();
        }
        
        if (game.getSettings().isMultiplier()) {
            credits *= gamePlayer.getRank().getMultiplier();
            xp *= gamePlayer.getRank().getMultiplier();
            if (gamePlayer.getRank().isNexiteBoost()) {
                nexites += gamePlayer.getRank().getMultiplier();
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
    
    public double getNexites() {
        return nexites;
    }
}
