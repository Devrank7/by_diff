package com.example.examplemod.mixins.screen;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.network.DifficultyHandler;
import com.example.examplemod.network.ModMessage;
import com.example.examplemod.network.PChangeDifficult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.ArrayList;
import java.util.Optional;

@Mixin(OptionsScreen.class)
public class MixOptionalScreen {
    public int width = 427;
    public int height = 240;
    @Shadow
    @Final
    private Options options;
    @Shadow
    private CycleButton<DifficultyGeneral> difficultyButton;
    @Shadow
    private LockIconButton lockButton;

    @Shadow
    private void lockCallback(boolean p_96261_) {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private LayoutElement createOnlineButton() {
        OptionsScreen screen = (OptionsScreen) (Object) this;
        if (screen.getMinecraft().level != null && screen.getMinecraft().hasSingleplayerServer()) {
            difficultyButton = myDifficultyButton(0, 0, "options.difficulty", screen.getMinecraft());
            if (!screen.getMinecraft().level.getLevelData().isHardcore()) {
                lockButton = new LockIconButton(0, 0, (p_280806_) -> {
                    screen.getMinecraft().setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", screen.getMinecraft().level.getLevelData().getDifficulty().getDisplayName())));
                });
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
                this.lockButton.setLocked(screen.getMinecraft().level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
                EqualSpacingLayout equalspacinglayout = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
                equalspacinglayout.addChild(this.difficultyButton);
                equalspacinglayout.addChild(this.lockButton);
                return equalspacinglayout;
            } else {
                this.difficultyButton.active = false;
                return this.difficultyButton;
            }
        } else {
            return Button.builder(Component.translatable("options.online"), (p_280805_) -> {
                screen.getMinecraft().setScreen(new OnlineOptionsScreen(screen, options));
            }).bounds(width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
        }
    }

    private CycleButton<DifficultyGeneral> myDifficultyButton(int p_262051_, int p_261805_, String p_261598_, Minecraft p_261922_) {
        DifficultyGeneral serverDifficulty = getServerDifficulty();
        return CycleButton.builder(DifficultyGeneral::getName).withValues(DifficultyGeneral.values()).withInitialValue(serverDifficulty).create(p_262051_, p_261805_, 150, 20, Component.translatable(p_261598_), (p_296186_, p_296187_) -> {
            DifficultyHandler.isNeedToUpdate = true;
            ModMessage.sendToServer(new PChangeDifficult(p_296187_));
        });
    }

    private DifficultyGeneral getServerDifficulty() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null) {
            MinecraftServer server = mc.getSingleplayerServer();
            ServerLevel serverLevel = server.overworld();
            LevelSettings l = serverLevel.getServer().getWorldData().getLevelSettings();
            return ((ILevelSettings) (Object) l).getDifficultyGen();
        }
        return DifficultyGeneral.NORMAL; // или значение по умолчанию
    }

}
