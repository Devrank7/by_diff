package com.example.examplemod.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class WorldsTab extends GridLayoutTab {
    private static final Component TITLE = Component.translatable("createWorld.tab.world.title");
    private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
    private static final Component GENERATE_STRUCTURES = Component.translatable("selectWorld.mapFeatures");
    private static final Component GENERATE_STRUCTURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
    private static final Component BONUS_CHEST = Component.translatable("selectWorld.bonusItems");
    private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
    static final Component SEED_EMPTY_HINT = Component.translatable("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
    private static final int WORLD_TAB_WIDTH = 310;
    private final EditBox seedEdit;
    private final Button customizeTypeButton;

    public WorldsTab(CreateWorldScreen screen) {
        super(TITLE);
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
        CycleButton<WorldCreationUiState.WorldTypeEntry> cyclebutton = gridlayout$rowhelper.addChild(
                CycleButton.builder(WorldCreationUiState.WorldTypeEntry::describePreset)
                        .withValues(this.createWorldTypeValueSupplier(screen))
                        .withCustomNarration(WorldsTab::createTypeButtonNarration)
                        .create(
                                0,
                                0,
                                150,
                                20,
                                Component.translatable("selectWorld.mapType"),
                                (p_268242_, p_267954_) -> screen.getUiState().setWorldType(p_267954_)
                        )
        );
        cyclebutton.setValue(screen.getUiState().getWorldType());
        screen.getUiState().addListener(p_280909_ -> {
            WorldCreationUiState.WorldTypeEntry worldcreationuistate$worldtypeentry = p_280909_.getWorldType();
            cyclebutton.setValue(worldcreationuistate$worldtypeentry);
            if (worldcreationuistate$worldtypeentry.isAmplified()) {
                cyclebutton.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
            } else {
                cyclebutton.setTooltip(null);
            }

            cyclebutton.active = screen.getUiState().getWorldType().preset() != null;
        });
        this.customizeTypeButton = gridlayout$rowhelper.addChild(
                Button.builder(Component.translatable("selectWorld.customizeType"), p_268355_ -> this.openPresetEditor(screen)).build()
        );
        screen.getUiState().addListener(p_280910_ -> this.customizeTypeButton.active = !p_280910_.isDebug() && p_280910_.getPresetEditor() != null);
        this.seedEdit = new EditBox(screen.getMinecraft().font, 308, 20, Component.translatable("selectWorld.enterSeed")) {
            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(WorldsTab.SEED_EMPTY_HINT);
            }
        };
        this.seedEdit.setHint(SEED_EMPTY_HINT);
        this.seedEdit.setValue(screen.getUiState().getSeed());
        this.seedEdit.setResponder(p_268342_ -> screen.getUiState().setSeed(this.seedEdit.getValue()));
        gridlayout$rowhelper.addChild(CommonLayouts.labeledElement(screen.getMinecraft().font, this.seedEdit, SEED_LABEL), 2);
        //
        try {
            Class<?> switchGridClass = Class.forName("net.minecraft.client.gui.screens.worldselection.SwitchGrid");
            Class<?> switchGridBuilderClass = Class.forName("net.minecraft.client.gui.screens.worldselection.SwitchGrid$Builder");

            // Получение конструктора SwitchGrid.Builder
            Constructor<?> switchGridBuilderConstructor = switchGridBuilderClass.getDeclaredConstructor(int.class);
            switchGridBuilderConstructor.setAccessible(true);
            Object switchGridBuilder = switchGridBuilderConstructor.newInstance(WORLD_TAB_WIDTH);

            // Получение метода addSwitch
            Method addSwitchMethod = switchGridBuilderClass.getDeclaredMethod("addSwitch", Component.class, BooleanSupplier.class, Consumer.class);
            addSwitchMethod.setAccessible(true);

            // Вызов метода addSwitch
            addSwitchMethod.invoke(switchGridBuilder, GENERATE_STRUCTURES, (BooleanSupplier) screen.getUiState()::isGenerateStructures, (Consumer<Boolean>) screen.getUiState()::setGenerateStructures);
            addSwitchMethod.invoke(switchGridBuilder, BONUS_CHEST, (BooleanSupplier) screen.getUiState()::isBonusChest, (Consumer<Boolean>) screen.getUiState()::setBonusChest);

            // Получение метода build
            Method buildMethod = switchGridBuilderClass.getDeclaredMethod("build", Consumer.class);
            buildMethod.setAccessible(true);

            // Вызов метода build
            Object switchGrid = buildMethod.invoke(switchGridBuilder, (Consumer<Object>) obj -> gridlayout$rowhelper.addChild((GridLayout) obj, 2));

            // Получение метода refreshStates
            Method refreshStatesMethod = switchGridClass.getDeclaredMethod("refreshStates");
            refreshStatesMethod.setAccessible(true);

            // Вызов метода refreshStates
            screen.getUiState().addListener((p_268209_) -> {
                try {
                    refreshStatesMethod.invoke(switchGrid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPresetEditor(CreateWorldScreen screen) {
        PresetEditor preseteditor = screen.getUiState().getPresetEditor();
        if (preseteditor != null) {
            screen.getMinecraft().setScreen(preseteditor.createEditScreen(screen, screen.getUiState().getSettings()));
        }
    }

    private CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry> createWorldTypeValueSupplier(CreateWorldScreen screen) {
        return new CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry>() {
            @Override
            public List<WorldCreationUiState.WorldTypeEntry> getSelectedList() {
                return CycleButton.DEFAULT_ALT_LIST_SELECTOR.getAsBoolean() ? screen.getUiState().getAltPresetList() : screen.getUiState().getNormalPresetList();
            }

            @Override
            public List<WorldCreationUiState.WorldTypeEntry> getDefaultList() {
                return screen.getUiState().getNormalPresetList();
            }
        };
    }

    private static MutableComponent createTypeButtonNarration(CycleButton<WorldCreationUiState.WorldTypeEntry> p_268292_) {
        return p_268292_.getValue().isAmplified() ? CommonComponents.joinForNarration(p_268292_.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT) : p_268292_.createDefaultNarrationMessage();
    }
}
