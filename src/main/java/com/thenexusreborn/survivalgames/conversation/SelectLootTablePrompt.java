package com.thenexusreborn.survivalgames.conversation;

import com.stardevllc.colors.StarColors;
import com.stardevllc.starui.GuiManager;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.PlayerManageBuilder;
import com.thenexusreborn.survivalgames.loot.tables.SGLootTable;
import com.thenexusreborn.survivalgames.menu.manage.PlayerManageMenu;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class SelectLootTablePrompt extends StringPrompt {

    private SurvivalGames plugin;
    private PlayerManageMenu menu;
    private PlayerManageBuilder builder;

    public SelectLootTablePrompt(SurvivalGames plugin, PlayerManageMenu menu, PlayerManageBuilder builder) {
        this.plugin = plugin;
        this.menu = menu;
        this.builder = builder;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return StarColors.color(MsgType.INFO.format("Enter a loot table and number of items in the format: %v", "<loottable>:<numberOfItems>"));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!(context.getForWhom() instanceof Player player)) {
            return Prompt.END_OF_CONVERSATION;
        }

        GuiManager guiManager = Bukkit.getServicesManager().getRegistration(GuiManager.class).getProvider();
        
        if (input.equalsIgnoreCase("cancel")) {
            MsgType.INFO.send(player, "You cancelled setting the loot table");
            Bukkit.getScheduler().runTaskLater(plugin, () -> guiManager.openGUI(menu, player), 1L);
            return Prompt.END_OF_CONVERSATION;
        }
        
        SGLootTable lootTable;
        int amountOfItems;
        String[] lootSplit = input.split(":");
        if (lootSplit.length != 2) {
            player.sendMessage(MsgType.WARN.format("Invalid loot format. Must be %v", "<loottable>:<amount>"));
            return this;
        }

        lootTable = plugin.getLootManager().getLootTable(lootSplit[0]);
        if (lootTable == null) {
            player.sendMessage(MsgType.WARN.format("Unknown loot table %v.", lootSplit[0]));
            return this;
        }

        try {
            amountOfItems = Integer.parseInt(lootSplit[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(MsgType.WARN.format("Invalid whole number %v.", lootSplit[1]));
            return this;
        }
        
        builder.lootTable(lootTable);
        builder.numberOfItems(amountOfItems);
        return Prompt.END_OF_CONVERSATION;
    }
}
