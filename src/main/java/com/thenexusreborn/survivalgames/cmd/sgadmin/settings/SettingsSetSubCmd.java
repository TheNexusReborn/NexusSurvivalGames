package com.thenexusreborn.survivalgames.cmd.sgadmin.settings;

import com.stardevllc.converter.string.StringConverter;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.cmdflags.type.PresenceFlag;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class SettingsSetSubCmd extends SubCommand<SurvivalGames> {
    
    private static final PresenceFlag SILENT = new PresenceFlag("s", "Silent");
    
    private String type;
    private Function<SGPlayer, Object> instanceGetter;
    
    public SettingsSetSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String type, Function<SGPlayer, Object> instanceGetter) {
        super(plugin, parent, 2, "set", "", Rank.ADMIN, "s");
        this.type = type;
        this.instanceGetter = instanceGetter;
        this.cmdFlags.addFlag(SILENT);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            MsgType.WARN.send(sender, "Invalid argument count. Must provide a setting name followed by the new value");
            return true;
        }
        
        Map<Field, StringConverter<?>> fields = SGASettingsCmd.settingsFields.get(this.type);
        
        String setting = args[0].toLowerCase();
        Field field = null;
        StringConverter<?> converter = null;
        
        for (Entry<Field, StringConverter<?>> entry : fields.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(setting)) {
                field = entry.getKey();
                converter = entry.getValue();
                break;
            }
        }
        
        if (field == null) {
            MsgType.WARN.send(sender, "Could not find a setting named %v.", args[0]);
            return true;
        }
        
        if (converter == null) {
            MsgType.WARN.send(sender, "Could not find a converter for the type %v.", field.getType().getSimpleName());
            MsgType.WARN.send(sender, "Please report to Firestar311.");
            return true;
        }
        
        Object value = converter.convertTo(args[1]);
        
        if (value == null) {
            MsgType.WARN.send(sender, "Could not convert %v to a(n) %v.", args[1], field.getType().getSimpleName());
            return true;
        }
        
        Player player = (Player) sender;
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        Object settingsInstance = instanceGetter.apply(sgPlayer);
        if (settingsInstance == null) {
            return true;
        }
        
        try {
            field.set(settingsInstance, value);
        } catch (Exception e) {
            MsgType.ERROR.send(player, "Failed to set the %v setting to %v.", type, value);
            e.printStackTrace();
            return true;
        }
        if (!flagResults.isPresent(SILENT)) {
            plugin.getNexusCore().getStaffChannel().sendMessage(new ChatContext(sgPlayer.getNexusPlayer().getColoredName() + " &fset the " + type + " setting " + field.getName() + " to " + value + "."));
        } else {
            MsgType.INFO.send(player, "You set the %v setting %v to %v.", type, field.getName(), value);
        }
        
        return true;
    }
}