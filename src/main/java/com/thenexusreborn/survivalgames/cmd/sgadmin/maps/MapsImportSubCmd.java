package com.thenexusreborn.survivalgames.cmd.sgadmin.maps;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.gamemaps.MapManager;
import com.thenexusreborn.gamemaps.YamlMapManager;
import com.thenexusreborn.gamemaps.model.SGMap;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.map.SQLMapManager;
import org.bukkit.command.CommandSender;

public class MapsImportSubCmd extends SubCommand<SurvivalGames> {
    public MapsImportSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, 1, "import", "", Rank.ADMIN, "i");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a data type, SQL or YML");
            return true;
        }
        
        String option = args[0];
        if (!(option.equalsIgnoreCase("sql") || option.equalsIgnoreCase("yml"))) {
            sender.sendMessage(MsgType.WARN.format("Invalid option, only valid options are sql and yml."));
            return true;
        }
        
        MapManager importManager;
        
        if (option.equalsIgnoreCase("sql")) {
            importManager = new SQLMapManager(plugin);
        } else {
            importManager = new YamlMapManager(plugin);
        }
        
        importManager.loadMaps();
        if (importManager.getMaps().isEmpty()) {
            sender.sendMessage(MsgType.WARN.format("No maps could be loaded from %v.", option.toUpperCase()));
            return true;
        }
        
        int newMaps = 0, duplicateMaps = 0;
        for (SGMap map : importManager.getMaps()) {
            SGMap existingMap = plugin.getMapManager().getMap(map.getName());
            if (existingMap == null) {
                plugin.getMapManager().addMap(map);
                sender.sendMessage(MsgType.INFO.format("Added %v as a new map.", map.getName()));
                newMaps++;
            } else {
                existingMap.copyFrom(map);
                sender.sendMessage(MsgType.INFO.format("Replaced %v's settings with the values from the imported data.", map.getName()));
                duplicateMaps++;
            }
        }
        
        sender.sendMessage(MsgType.INFO.format("Added %v new map(s) and updated %v map(s).", newMaps, duplicateMaps));
        return true;
    }
}
