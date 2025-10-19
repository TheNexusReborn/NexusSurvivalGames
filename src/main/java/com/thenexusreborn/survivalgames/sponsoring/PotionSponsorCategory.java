package com.thenexusreborn.survivalgames.sponsoring;

import com.stardevllc.smaterial.SMaterial;
import com.stardevllc.starmclib.names.PotionNames;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PotionSponsorCategory extends SponsorCategory<PotionEffectType> {
    public PotionSponsorCategory(String name, SMaterial icon) {
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
