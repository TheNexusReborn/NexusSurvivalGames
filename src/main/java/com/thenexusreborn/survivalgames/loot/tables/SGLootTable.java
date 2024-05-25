package com.thenexusreborn.survivalgames.loot.tables;

import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SGLootTable extends LootTable {
    
    protected SGPlayer player;
    
    protected File file;
    protected FileConfiguration config;
    
    protected boolean reloading = false;
    
    public SGLootTable(SGPlayer sgPlayer, String name, File configFile) {
        super(name);
        this.player = sgPlayer;
        
        if (configFile != null) {

            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            this.file = configFile;
        }
    }
    
    public void saveData() {
        if (this.file == null || this.config == null) {
            return;
        }
        
        this.file.delete();
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        this.config = YamlConfiguration.loadConfiguration(this.file);

        for (Map.Entry<String, Integer> entry : this.itemWeights.entrySet()) {
            this.config.set(entry.getKey(), entry.getValue());
        }

        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void loadData() {
        if (this.file == null) {
            return;
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        
        resetItems();
        
        List<String> removeKeys = new ArrayList<>();

        for (String itemName : this.config.getKeys(false)) {
            int weight = this.config.getInt(itemName);
            LootItem item = Items.REGISTRY.get(itemName);
            if (item == null) {
                SurvivalGames.getInstance().getLogger().warning("Loot Table " + getName() + " had an invalid item entry " + itemName);
                removeKeys.add(itemName);
                continue;
            }
            addItem(item, weight);
        }
        
        removeKeys.forEach(key -> config.set(key, null));
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public abstract void loadDefaultData();

    public SGLootTable(String name, File file) {
        this(null, name, file);
    }
    
    public SGLootTable(String name) {
        this(null, name, null);
    }

    public SGPlayer getPlayer() {
        return player;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public boolean isReloading() {
        return reloading;
    }
}
