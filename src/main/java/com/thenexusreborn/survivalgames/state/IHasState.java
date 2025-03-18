package com.thenexusreborn.survivalgames.state;

public interface IHasState {
    IState getState();
    
    default ISubState getSubState() {
        return null;
    }
    
    default void setState(IState state) {
        
    }
    
    default void setSubState(ISubState subState) {
        
    }
}
