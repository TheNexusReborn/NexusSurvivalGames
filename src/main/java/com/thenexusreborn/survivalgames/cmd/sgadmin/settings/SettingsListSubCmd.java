package com.thenexusreborn.survivalgames.cmd.sgadmin.settings;

import com.stardevllc.cmdflags.FlagResult;
import com.stardevllc.colors.StarColors;
import com.stardevllc.converter.string.StringConverter;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class SettingsListSubCmd extends SubCommand<SurvivalGames> {
    
    private String type;
    private Function<SGPlayer, Object> instanceGetter;
    
    public SettingsListSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String type, Function<SGPlayer, Object> instanceGetter) {
        super(plugin, parent, 2, "list", "", Rank.ADMIN, "l");
        this.type = type;
        this.instanceGetter = instanceGetter;
        
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Map<Field, StringConverter<?>> fields = SGASettingsCmd.settingsFields.get(this.type);
        
        Player player = (Player) sender; 
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        Object settingsInstance = instanceGetter.apply(sgPlayer);
        
        if (instanceGetter == null) {
            return true;
        }
        
        List<String> lines = new LinkedList<>();
        for (Field field : fields.keySet()) {
            try {
                String line = "  &8 - &a" + field.getName().toLowerCase();
                if (settingsInstance != null) {
                    line += " &7= &e" + fields.get(field).convertFrom(field.get(settingsInstance));
                }
                lines.add(line);
            } catch (IllegalAccessException e) {
                MsgType.WARN.send(sender, "Could not parse the value of %v.", field.getName());
            }
        }
        
        if (lines.isEmpty()) {
            sender.sendMessage(MsgType.WARN.format("Could not find any settings to list."));
            return true;
        }
        
        sender.sendMessage(MsgType.INFO.format("List of &b" + type + " settings."));
        
        int page = 1;
        
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[3]);
            } catch (Exception e) {
                MsgType.WARN.send(sender, "Invalid number %v", args[3]);
                return true;
            }
        }
        
        int offset = (page - 1) * 7; // 7 is the amount per page
        
        int remaining = lines.size() - offset;
        
        int offsetEnd = offset + Math.min(7, remaining);
        
        for (int i = 0; i < offsetEnd; i++) {
            StarColors.coloredMessage(sender, lines.get(offset + i));
        }
        
        return true;
    }
}
