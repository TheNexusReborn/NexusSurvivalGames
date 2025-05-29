package com.thenexusreborn.survivalgames.cmd.sgadmin.settings;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.menu.GameSettingsMenu;
import com.thenexusreborn.survivalgames.settings.GameSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class SettingsMenuSubCmd extends SubCommand<SurvivalGames> {
    
    private String type;
    private Function<SGPlayer, Object> instanceGetter;
    private GuiManager guiManager;
    
    public SettingsMenuSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent, String type, Function<SGPlayer, Object> instanceGetter) {
        super(plugin, parent, 2, "menu", "", Rank.ADMIN, "m");
        this.type = type;
        this.instanceGetter = instanceGetter;
        this.playerOnly = true;
        this.guiManager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender; 
        SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
        
        if (instanceGetter == null) {
            return true;
        }
        
        Object settingsInstance = instanceGetter.apply(sgPlayer);
        
        if (settingsInstance == null) {
            return true;
        }
        
        guiManager.openGUI(new GameSettingsMenu(player.getUniqueId(), (GameSettings) settingsInstance), player);
        return true;
    }
}
