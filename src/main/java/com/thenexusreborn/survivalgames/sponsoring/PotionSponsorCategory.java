package com.thenexusreborn.survivalgames.sponsoring;

import com.cryptomorin.xseries.XMaterial;
import com.stardevllc.starcore.utils.PotionNames;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

import java.util.*;

public class PotionSponsorCategory extends SponsorCategory<PotionEffectType> {
    public PotionSponsorCategory(String name, XMaterial icon) {
        super(name, icon);
    }
    
    @Override
    public void apply(Player player, Object entry) {
        PotionEffect effect = ((PotionEffectType) entry).createEffect((new Random().nextInt(16) + 5) * 20, new Random().nextInt(4) + 1);
        player.addPotionEffect(effect);
    }
    
    @Override
    public List<String> getListOfEntries() {
        List<String> entriesList = new LinkedList<>();
        for (PotionEffectType entry : this.getEntries()) {
            entriesList.add(PotionNames.getDefaultName(entry));
        }
        return entriesList;
    }
}
