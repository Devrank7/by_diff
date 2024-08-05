package com.example.examplemod.tabs;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.WorldDataConfiguration;

import java.lang.reflect.Method;

public class MoresTab extends GridLayoutTab {

    private static final Component TITLE = Component.translatable("createWorld.tab.more.title");
    private static final Component GAME_RULES_LABEL = Component.translatable("selectWorld.gameRules");
    private static final Component DATA_PACKS_LABEL = Component.translatable("selectWorld.dataPacks");

    public MoresTab(CreateWorldScreen screen) {
        super(TITLE);
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(1);
        gridlayout$rowhelper.addChild(Button.builder(GAME_RULES_LABEL, p_268028_ -> this.openGameRulesScreen(screen)).width(210).build());
        gridlayout$rowhelper.addChild(
                Button.builder(
                                Component.translatable("selectWorld.experiments"), p_269642_ -> {
                                    try {
                                        Method method = screen.getClass().getDeclaredMethod("openDataPackSelectionScreen", WorldDataConfiguration.class);
                                        method.setAccessible(true);
                                        method.invoke(screen, screen.getUiState().getSettings().dataConfiguration());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                        )
                        .width(210)
                        .build()
        );
        gridlayout$rowhelper.addChild(
                Button.builder(DATA_PACKS_LABEL, p_268345_ -> {
                            try {
                                Method method = screen.getClass().getDeclaredMethod("openDataPackSelectionScreen", WorldDataConfiguration.class);
                                method.setAccessible(true);
                                method.invoke(screen, screen.getUiState().getSettings().dataConfiguration());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .width(210)
                        .build()
        );
    }

    private void openGameRulesScreen(CreateWorldScreen screen) {
        screen.getMinecraft().setScreen(new EditGameRulesScreen(screen.getUiState().getGameRules().copy(), p_268107_ -> {
            screen.getMinecraft().setScreen(screen);
            p_268107_.ifPresent(screen.getUiState()::setGameRules);
        }));
    }
}
