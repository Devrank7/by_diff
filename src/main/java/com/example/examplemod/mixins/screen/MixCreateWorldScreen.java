package com.example.examplemod.mixins.screen;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.intrtfaces.IWorldTab;
import com.example.examplemod.tabs.GamesTab;
import com.example.examplemod.tabs.MoresTab;
import com.example.examplemod.tabs.WorldsTab;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class MixCreateWorldScreen extends Screen implements IWorldTab {
    @Shadow(remap = false)
    private TabNavigationBar tabNavigationBar;
    @Final
    @Shadow(remap = false)
    private TabManager tabManager;

    @Unique
    public int width = 427;
    @Final
    @Shadow(remap = false)
    WorldCreationUiState uiState;

    @Shadow(remap = false)
    protected abstract void onCreate();

    @Shadow(remap = false)
    public abstract void popScreen();

    @Shadow(remap = false)
    public abstract void repositionElements();
    @Shadow(remap = false)
    @Final
    private HeaderAndFooterLayout layout;

    private DifficultyGeneral difficulty_gen = DifficultyGeneral.NORMAL;


    public MixCreateWorldScreen(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(at = @At("HEAD"), method = "init", cancellable = true, remap = false)
    public void init(CallbackInfo ci) {
        ci.cancel();
        CreateWorldScreen screen = (CreateWorldScreen) (Object) this;
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(new GamesTab(screen), new WorldsTab(screen), new MoresTab(screen))
                .build();
        this.addRenderableWidget(this.tabNavigationBar);
        LinearLayout linearlayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(Button.builder(Component.translatable("selectWorld.create"), p_232938_ -> this.onCreate()).build());
        linearlayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, p_232903_ -> this.popScreen()).build());
        this.layout.visitWidgets(p_267851_ -> {
            p_267851_.setTabOrderGroup(1);
            this.addRenderableWidget(p_267851_);
        });
        this.tabNavigationBar.selectTab(0, false);
        this.uiState.onChanged();
        this.repositionElements();
    }
    /**
     * @author TheIllusiveC4
     * @reason TheIllusiveC4
     */
    @Overwrite
    private LevelSettings createLevelSettings(boolean p_205448_) {
        String s = this.uiState.getName().trim();
        if (p_205448_) {
            GameRules gamerules = new GameRules();
            gamerules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            LevelSettings levelSettings = new LevelSettings(s, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, WorldDataConfiguration.DEFAULT);
            ((ILevelSettings) (Object) levelSettings).setDifficultyGen(getDifficultyGen());
            System.out.println("createLevelSettings called -");
            return levelSettings;
        } else {
            LevelSettings levelSettings = new LevelSettings(
                    s,
                    this.uiState.getGameMode().gameType,
                    this.uiState.isHardcore(),
                    this.uiState.getDifficulty(),
                    this.uiState.isAllowCommands(),
                    this.uiState.getGameRules(),
                    this.uiState.getSettings().dataConfiguration()
            );
            ((ILevelSettings) (Object) levelSettings).setDifficultyGen(getDifficultyGen());
            System.out.println("createLevelSettings called");
            System.out.println(((ILevelSettings) (Object) levelSettings).getDifficultyGen());
            return levelSettings;
        }
    }

    @Override
    public void setDifficultyGen(DifficultyGeneral difficulty) {
        CreateWorldScreen screen = (CreateWorldScreen) (Object) this;
        screen.getUiState().setDifficulty(DifficultyGeneral.getSimpleDifficulty(difficulty));
        difficulty_gen = difficulty;
    }

    @Override
    public DifficultyGeneral getDifficultyGen() {
        return difficulty_gen;
    }
}
