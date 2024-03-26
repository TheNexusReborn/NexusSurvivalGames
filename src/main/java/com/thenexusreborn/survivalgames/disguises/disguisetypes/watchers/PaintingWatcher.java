package com.thenexusreborn.survivalgames.disguises.disguisetypes.watchers;

import com.thenexusreborn.survivalgames.disguises.disguisetypes.Disguise;
import com.thenexusreborn.survivalgames.disguises.disguisetypes.FlagWatcher;
import com.thenexusreborn.survivalgames.disguises.utilities.DisguiseUtilities;
import org.bukkit.Art;

public class PaintingWatcher extends FlagWatcher {

    private Art painting;

    public PaintingWatcher(Disguise disguise) {
        super(disguise);
    }

    @Override
    public PaintingWatcher clone(Disguise disguise) {
        PaintingWatcher watcher = (PaintingWatcher) super.clone(disguise);
        watcher.setArt(getArt());
        return watcher;
    }

    public Art getArt() {
        return painting;
    }

    public void setArt(Art newPainting) {
        this.painting = newPainting;
        if (getDisguise().getEntity() != null && getDisguise().getWatcher() == this) {
            DisguiseUtilities.refreshTrackers(getDisguise());
        }
    }

}
