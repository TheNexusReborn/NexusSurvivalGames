package com.thenexusreborn.survivalgames.menu;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starui.GuiManager;
import com.stardevllc.starui.element.button.Button;
import com.stardevllc.starui.gui.InventoryGUI;
import com.stardevllc.starui.gui.UpdatingGUI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.reflection.impl.PlayerSkull;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.survivalgames.SGPlayer;
import com.thenexusreborn.survivalgames.SurvivalGames;
import com.thenexusreborn.survivalgames.game.Game;
import com.thenexusreborn.survivalgames.game.GamePlayer;
import com.thenexusreborn.survivalgames.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class TeamMenu extends InventoryGUI implements UpdatingGUI {

    private GuiManager manager;

    private SurvivalGames plugin;
    private Game game;
    private GameTeam team;

    public TeamMenu(SurvivalGames plugin, GameTeam team, Game game, UUID player) {
        super(3, StarColors.color(team.getColor() + team.getName()), player);
        manager = plugin.getServer().getServicesManager().getRegistration(GuiManager.class).getProvider();
        this.plugin = plugin;
        this.game = game;
        this.team = team;
    }

    @Override
    public void createItems() {
        if (game != null) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                if (gamePlayer.getToggleValue("vanish")) {
                    continue;
                }

                if (gamePlayer.getTeam() != team) {
                    continue;
                }

                GamePlayer actorPlayer = game.getPlayer(this.playerUUID);
                boolean canViewVitalsAndTeleport = actorPlayer != null && (actorPlayer.getTeam() == GameTeam.SPECTATORS || actorPlayer.getRank().ordinal() <= Rank.ADMIN.ordinal());

                Button button = new Button().iconCreator(p -> {
                    ItemStack skull = new PlayerSkull().getSkull(Bukkit.getPlayer(gamePlayer.getUniqueId()));
                    ItemMeta meta = skull.getItemMeta();
                    meta.setDisplayName(StarColors.color(gamePlayer.getColoredName()));

                    
                    List<String> lore = new LinkedList<>();
                    if (canViewVitalsAndTeleport) {
                        lore.add("&8/ / / / / &7&lVITALS &8/ / / / /");
                        lore.addAll(gamePlayer.getMenuVitals());
                    }
                    lore.add("&8/ / / / / &7&lSTATS &8/ / / / /");
                    lore.addAll(gamePlayer.getMenuStats());

                    lore.add("");
                    lore.add("&6&lLeft Click &fto open the menu");

                    if (canViewVitalsAndTeleport) {
                        lore.add("&6&lRight Click &fto teleport");
                    }

                    List<String> coloredLore = new LinkedList<>();
                    lore.forEach(line -> coloredLore.add(StarColors.color(line)));
                    meta.setLore(coloredLore);
                    skull.setItemMeta(meta);
                    return skull;
                }).consumer(e -> {
                    if (e.getClick() == ClickType.LEFT) {
                        manager.openGUI(new PlayerMenu(plugin, this.playerUUID, gamePlayer), e.getWhoClicked());
                    } else if (e.getClick() == ClickType.RIGHT) {
                        if (!canViewVitalsAndTeleport) {
                            MsgType.WARN.send(e.getWhoClicked(), "You can't do that.");
                            return;
                        }

                        e.getWhoClicked().teleport(Bukkit.getPlayer(gamePlayer.getUniqueId()).getLocation());
                        SGPlayer actor = plugin.getPlayerRegistry().get(e.getWhoClicked().getUniqueId());
                        actor.getGamePlayer().setPosition(e.getWhoClicked().getLocation());
                        e.getWhoClicked().sendMessage(StarColors.color(MsgType.INFO + "Teleported to " + gamePlayer.getColoredName() + "&e."));
                    }
                });
                addElement(button);
            }
        }
    }
}
