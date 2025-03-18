package com.thenexusreborn.survivalgames;

import com.thenexusreborn.survivalgames.control.ControlType;

public interface Controllable {
    ControlType getControlType();
    void setControlType(ControlType controlType);
}
