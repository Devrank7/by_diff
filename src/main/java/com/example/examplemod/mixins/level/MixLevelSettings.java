package com.example.examplemod.mixins.level;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelSettings.class)
public class MixLevelSettings implements ILevelSettings {
    @Unique
    private DifficultyGeneral difficulty_gen = DifficultyGeneral.NORMAL;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public LevelSettings copy() {
        LevelSettings levelSettings = (LevelSettings) (Object) this;
        DifficultyGeneral DifficultyGeneral = ((ILevelSettings) (Object) levelSettings).getDifficultyGen();
        LevelSettings levelSettings1 = new LevelSettings(levelSettings.levelName(), levelSettings.gameType(), levelSettings.hardcore(), levelSettings.difficulty(), levelSettings.allowCommands(), levelSettings.gameRules().copy(), levelSettings.getDataConfiguration(), levelSettings.getLifecycle());
        ((ILevelSettings) (Object) levelSettings1).setDifficultyGen(DifficultyGeneral);
        return levelSettings1;
    }

    /**
     * @author Devlink
     * @reason Redifficult
     */
    @Overwrite
    public LevelSettings withGameType(GameType p_46923_) {
        LevelSettings levelSettings = (LevelSettings) (Object) this;
        DifficultyGeneral DifficultyGeneral = ((ILevelSettings) (Object) levelSettings).getDifficultyGen();
        System.err.println("Copied lvl data = 1 " + DifficultyGeneral);
        LevelSettings l = new LevelSettings(levelSettings.levelName(), p_46923_, levelSettings.hardcore(), levelSettings.difficulty(), levelSettings.allowCommands(), levelSettings.gameRules().copy(), levelSettings.getDataConfiguration(), levelSettings.getLifecycle());
        ((ILevelSettings) (Object) l).setDifficultyGen(DifficultyGeneral);
        return l;
    }

    /**
     * @author Devlink
     * @reason Redifficult
     */
    @Overwrite
    public LevelSettings withDifficulty(Difficulty p_46919_) {
        LevelSettings levelSettings = (LevelSettings) (Object) this;
        DifficultyGeneral DifficultyGeneral = ((ILevelSettings) (Object) levelSettings).getDifficultyGen();
        System.err.println("Copied lvl data = 2 " + DifficultyGeneral);
        //net.minecraftforge.common.ForgeHooks.onDifficultyChange(p_46919_, levelSettings.difficulty());
        LevelSettings l = new LevelSettings(levelSettings.levelName(), levelSettings.gameType(), levelSettings.hardcore(), p_46919_, levelSettings.allowCommands(), levelSettings.gameRules().copy(), levelSettings.getDataConfiguration(), levelSettings.getLifecycle());
        ((ILevelSettings) (Object) l).setDifficultyGen(DifficultyGeneral);
        return l;
    }

    /**
     * @author Devlink
     * @reason Redifficult
     */
    @Overwrite
    public LevelSettings withDataConfiguration(WorldDataConfiguration p_250867_) {
        LevelSettings levelSettings = (LevelSettings) (Object) this;
        DifficultyGeneral DifficultyGeneral = ((ILevelSettings) (Object) levelSettings).getDifficultyGen();
        System.err.println("Copied lvl data = 3 " + DifficultyGeneral);
        LevelSettings l = new LevelSettings(levelSettings.levelName(), levelSettings.gameType(), levelSettings.hardcore(), levelSettings.difficulty(), levelSettings.allowCommands(), levelSettings.gameRules().copy(), levelSettings.getDataConfiguration(), levelSettings.getLifecycle());
        ((ILevelSettings) (Object) l).setDifficultyGen(DifficultyGeneral);
        return l;
    }

    /**
     * @author Devlink
     * @reason Redifficult
     */
    @Overwrite
    public static LevelSettings parse(Dynamic<?> p_46925_, WorldDataConfiguration p_251697_) {
        System.err.println("Copied lvl data  = 4 ");
        GameType gametype = GameType.byId(p_46925_.get("GameType").asInt(0));
        LevelSettings l = new LevelSettings(p_46925_.get("LevelName").asString(""), gametype, p_46925_.get("hardcore").asBoolean(false), p_46925_.get("Difficulty").asNumber().map((p_46928_) -> {
            return Difficulty.byId(p_46928_.byteValue());
        }).result().orElse(Difficulty.NORMAL), p_46925_.get("allowCommands").asBoolean(gametype == GameType.CREATIVE), new GameRules(p_46925_.get("GameRules")), p_251697_, net.minecraftforge.common.ForgeHooks.parseLifecycle(p_46925_.get("forgeLifecycle").asString("stable")));
        DifficultyGeneral difficultyGeneral = DifficultyGeneral.byId(p_46925_.get("difficulty_gen").asInt(0));
        System.err.println("Copied lvl data  = 5 " + difficultyGeneral);
        ((ILevelSettings) (Object) l).setDifficultyGen(difficultyGeneral);
        // ServerLevelData
        return l;
    }

    @Override
    public DifficultyGeneral getDifficultyGen() {
        return difficulty_gen;
    }

    @Override
    public void setDifficultyGen(DifficultyGeneral difficultyGen) {
        difficulty_gen = difficultyGen;
    }

    @Override
    public LevelSettings withDifficultGen(DifficultyGeneral difficultGen) {
        LevelSettings levelSettings = (LevelSettings) (Object) this;
        Difficulty difficulty = DifficultyGeneral.getSimpleDifficulty(difficultGen);
        LevelSettings levelSettings1 = new LevelSettings(levelSettings.levelName(), levelSettings.gameType(), levelSettings.hardcore(), difficulty, levelSettings.allowCommands(), levelSettings.gameRules().copy(), levelSettings.getDataConfiguration(), levelSettings.getLifecycle());
        ((ILevelSettings) (Object) levelSettings1).setDifficultyGen(difficultGen);
        System.err.println("Copied lvl data = 7 " + difficultGen);
        return levelSettings1;
    }
}
