package com.example.examplemod.tabs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IWorldTab;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GamesTab extends GridLayoutTab {
    private static final Component TITLE = Component.translatable("createWorld.tab.game.title");
    private static final Component ALLOW_COMMANDS = Component.translatable("selectWorld.allowCommands.new");
    private final EditBox nameEdit;

    public GamesTab(CreateWorldScreen screen) {
        super(TITLE);
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(1);
        LayoutSettings layoutsettings = gridlayout$rowhelper.newCellSettings();
        this.nameEdit = new EditBox(screen.getMinecraft().font, 208, 20, Component.translatable("selectWorld.enterName"));
        this.nameEdit.setValue(screen.getUiState().getName());
        this.nameEdit.setResponder(screen.getUiState()::setName);
        screen.getUiState()
                .addListener(
                        p_275871_ -> this.nameEdit
                                .setTooltip(
                                        Tooltip.create(
                                                Component.translatable("selectWorld.targetFolder", Component.literal(p_275871_.getTargetFolder()).withStyle(ChatFormatting.ITALIC))
                                        )
                                )
                );
        try {
            // Доступ к защищенному методу setInitialFocus
            Method setInitialFocus = Screen.class.getDeclaredMethod("setInitialFocus", GuiEventListener.class);
            setInitialFocus.setAccessible(true);
            setInitialFocus.invoke(screen, this.nameEdit);

            // Доступ к защищенному полю NAME_LABEL
            Field nameLabelField = screen.getClass().getDeclaredField("NAME_LABEL");
            nameLabelField.setAccessible(true);
            Component nameLabel = (Component) nameLabelField.get(screen);
            gridlayout$rowhelper.addChild(CommonLayouts.labeledElement(screen.getMinecraft().font, this.nameEdit, nameLabel), gridlayout$rowhelper.newCellSettings().alignHorizontallyCenter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CycleButton<WorldCreationUiState.SelectedGameMode> cyclebutton = gridlayout$rowhelper.addChild(
                CycleButton.<WorldCreationUiState.SelectedGameMode>builder(p_268080_ -> p_268080_.displayName)
                        .withValues(
                                WorldCreationUiState.SelectedGameMode.SURVIVAL,
                                WorldCreationUiState.SelectedGameMode.HARDCORE,
                                WorldCreationUiState.SelectedGameMode.CREATIVE
                        )
                        .create(0, 0, 210, 20, Component.translatable("selectWorld.gameMode"), (p_268266_, p_268208_) -> screen.getUiState().setGameMode(p_268208_)),
                layoutsettings
        );
        screen.getUiState().addListener(p_280907_ -> {
            cyclebutton.setValue(p_280907_.getGameMode());
            cyclebutton.active = !p_280907_.isDebug();
            cyclebutton.setTooltip(Tooltip.create(p_280907_.getGameMode().getInfo()));
        });
        CycleButton<DifficultyGeneral> cyclebutton1 = gridlayout$rowhelper.addChild(
                CycleButton.builder(DifficultyGeneral::getName)
                        .withValues(DifficultyGeneral.values())
                        .create(
                                0,
                                0,
                                210,
                                20,
                                Component.translatable("options.difficulty"),
                                (p_267962_, p_268338_) -> {
                                    ((IWorldTab) (Object) screen).setDifficultyGen(p_268338_);
                                    screen.getUiState().onChanged();
                                }
                        ),
                layoutsettings
        );
        screen.getUiState().addListener(p_280905_ -> {
            cyclebutton1.setValue(((IWorldTab) (Object) screen).getDifficultyGen());
            cyclebutton1.active = !screen.getUiState().isHardcore();
            Component component = ((IWorldTab) (Object) screen).getDifficultyGen().getInfo();
            cyclebutton1.setTooltip(Tooltip.create(component));
        });
        CycleButton<Boolean> cyclebutton2 = gridlayout$rowhelper.addChild(
                CycleButton.onOffBuilder()
                        .withTooltip(p_325425_ -> Tooltip.create(Component.translatable("selectWorld.allowCommands.info")))
                        .create(0, 0, 210, 20, ALLOW_COMMANDS, (p_325426_, p_325427_) -> screen.getUiState().setAllowCommands(p_325427_))
        );
        screen.getUiState().addListener(p_325429_ -> {
            cyclebutton2.setValue(screen.getUiState().isAllowCommands());
            cyclebutton2.active = !screen.getUiState().isDebug() && !screen.getUiState().isHardcore();
        });
        if (!SharedConstants.getCurrentVersion().isStable()) {
            gridlayout$rowhelper.addChild(
                    Button.builder(
                                    Component.translatable("selectWorld.experiments"),
                                    p_269641_ -> {
                                        try {
                                            Method method = screen.getClass().getDeclaredMethod("openExperimentsScreen", WorldDataConfiguration.class);
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
        }
    }
}
