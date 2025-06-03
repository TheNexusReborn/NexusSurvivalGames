package com.thenexusreborn.survivalgames.cmd.sgadmin.game;

import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.*;
import com.thenexusreborn.survivalgames.gamelog.GameGiveAction;
import com.thenexusreborn.survivalgames.loot.item.Items;
import com.thenexusreborn.survivalgames.loot.item.LootItem;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameGiveAllSubCmd extends GameSubCommand {
    public GameGiveAllSubCmd(SurvivalGames plugin, ICommand<SurvivalGames> parent) {
        super(plugin, parent, "giveall", "", Rank.ADMIN, "ga");
    }
    
    @Override
    protected boolean handle(Player player, SGPlayer sgPlayer, Game game, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            player.sendMessage(MsgType.WARN.format("Usage: /survivalgames game give <item> [player]"));
            return true;
        }
        
        SGLootTable lootTable = null;
        LootItem lootItem = null;
        int amount = 0;
        if (args[0].contains(":")) {
            String[] split = args[0].split(":");
            if (split.length != 2) {
                MsgType.WARN.send(player, "Invalid loot table format. Must be <loottable>:<amount>");
                return true;
            }
            
            lootTable = plugin.getLootManager().getLootTable(split[0]);
            
            if (lootTable == null) {
                MsgType.WARN.send(player, "Invalid loot table %v.", split[0]);
                return true;
            }
            
            try {
                amount = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                MsgType.WARN.send(player, "Invalid whole number %v.", split[1]);
                return true;
            }
            
            if (amount < 1) {
                MsgType.WARN.send(player, "You must specify 1 or more for the amount.");
                return true;
            }
        } else {
            lootItem = Items.REGISTRY.get(args[0]);
            
            if (lootItem == null) {
                player.sendMessage(MsgType.WARN.format("Unknown item %v", args[0]));
                return true;
            }
        }
        
        if (lootItem != null) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                    continue;
                }
                
                Bukkit.getPlayer(gamePlayer.getUniqueId()).getInventory().addItem(lootItem.getItemStack());
            }
            game.getGameInfo().getActions().add(new GameGiveAction(player.getName(), "all", lootItem.getName().toLowerCase().replace(" ", "_")));
            game.sendMessage(MsgType.INFO.format("All players were given %v by %v", lootItem.getName(), player.getName()));
        } else if (lootTable != null && amount > 0) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getTeam() != GameTeam.TRIBUTES) {
                    continue;
                }
                
                List<ItemStack> loot = lootTable.generateLoot(amount);
                for (ItemStack itemStack : loot) {
                    Bukkit.getPlayer(gamePlayer.getUniqueId()).getInventory().addItem(itemStack);
                }
            }
            
            game.sendMessage(MsgType.INFO.format("All players were given %v items from the loot table &v by %v", amount, lootTable.getName(), player.getName()));
        } else {
            MsgType.SEVERE.send(player, "A problem occurred that shouldn't have with the give action. Please report to Firestar311");
            return true;
        }
        
        return true;
    }
}
