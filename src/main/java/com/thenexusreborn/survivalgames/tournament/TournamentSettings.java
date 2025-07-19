package com.thenexusreborn.survivalgames.tournament;

import com.thenexusreborn.survivalgames.settings.ISettings;

public class TournamentSettings implements ISettings {
    protected String name;
    
    protected int pointsPerKill, pointsPerMutationKill, winPoints, secondPlacePoints, thirdPlacePoints;
    
    @Override
    public ISettings clone() {
        try {
            return (TournamentSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return new TournamentSettings();
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
