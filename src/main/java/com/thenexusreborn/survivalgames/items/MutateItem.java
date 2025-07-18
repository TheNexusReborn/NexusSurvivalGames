package com.thenexusreborn.survivalgames.items;

import com.stardevllc.helper.Pair;
import com.stardevllc.starcore.api.itembuilder.ItemBuilder;
import com.stardevllc.staritems.model.CustomItem;
import com.stardevllc.staritems.model.types.PlayerEvent;
import com.stardevllc.starmclib.XMaterial;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.menu.MutateGui;
import com.thenexusreborn.survivalgames.mutations.MutationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class MutateItem extends CustomItem {
    
    private GuiManager guiManager;
    
    public MutateItem(SurvivalGames plugin) {
        super(plugin, "mutateitem", ItemBuilder.of(XMaterial.ROTTEN_FLESH));
        this.guiManager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        addEventHandler(PlayerEvent.INTERACT, e -> {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) {
                return;
            }
            
            Player player = e.getPlayer();
            SGPlayer sgPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
            if (sgPlayer == null) {
                return;
            }
            
            Game game = sgPlayer.getGame();
            
            if (game == null) {
                return;
            }
            
            GamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
            
            if (gamePlayer == null) {
                return;
            }
            
            if (gamePlayer.getTeam() != GameTeam.SPECTATORS) {
                return;
            }
            
            Pair<Boolean, String> canMutateResult = gamePlayer.canMutate();
            if (canMutateResult.key()) {
                MutationBuilder mutationBuilder = new MutationBuilder(gamePlayer);
                mutationBuilder.setUsePass(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> guiManager.openGUI(new MutateGui(plugin, mutationBuilder), player), 1L);
            } else {
                gamePlayer.sendMessage(MsgType.WARN + canMutateResult.value());
            }
        });
    }
}
