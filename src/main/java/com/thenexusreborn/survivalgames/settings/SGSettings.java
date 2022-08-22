package com.thenexusreborn.survivalgames.settings;

import com.thenexusreborn.api.data.annotations.Primary;

public abstract class SGSettings {
    @Primary
    protected long id;
    protected String type;
    
    public SGSettings() {
    }
    
    public SGSettings(String type) {
        this.type = type;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
