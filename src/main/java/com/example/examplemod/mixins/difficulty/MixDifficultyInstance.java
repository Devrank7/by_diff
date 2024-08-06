package com.example.examplemod.mixins.difficulty;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyInstance.class)
public class MixDifficultyInstance implements IDifficultyInstance {
    @Unique
    private DifficultyGeneral difficultyGeneral = DifficultyGeneral.NORMAL;
    @Shadow
    @Final
    @Mutable
    private Difficulty base;
    @Shadow
    @Final
    @Mutable
    private float effectiveDifficulty;
    @Unique
    private long dayTime;
    @Unique
    private long chunkTime;
    @Unique
    private float moonPhase;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    public void initOn(Difficulty p_19052_, long dayTime, long chunkTime, float moonBrightness, CallbackInfo info) {
        this.base = p_19052_;
        this.difficultyGeneral = DifficultyGeneral.NORMAL;
        this.dayTime = dayTime;
        this.chunkTime = chunkTime;
        this.moonPhase = moonBrightness;
        this.effectiveDifficulty = 1.0F;

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float getSpecialMultiplier() {
        if (this.effectiveDifficulty < 2.0F) {
            return 0.0F;
        } else {
            return this.effectiveDifficulty > 6.0F ? (this.effectiveDifficulty - 4.0F) / 2.0F : this.effectiveDifficulty > 4.0F ? 1.0F : (this.effectiveDifficulty - 2.0F) / 2.0F;
        }
    }

    /**
     * @author Devlink
     * @reason Cancels
     */
    @Overwrite
    private float calculateDifficulty(Difficulty p_19052_, long dayTime, long chunkTime, float moonBrightness) {
        return switch (difficultyGeneral) {
            case PEACEFUL -> 0.0F;
            case INSANE -> {
                float f = (((float) dayTime - 72000.0F) / 2328000.0F) + 1;
                float f1 = Mth.clamp(((float) dayTime - 72000.0F) / 1728000.0F, 0.0F, 2);
                float f2 = Mth.clamp((float) chunkTime / 1440000.0F, 0.0F, 2);
                float f3 = Mth.clamp(moonBrightness * f, 0.0F, f);
                float sum = f1 + f2 + f3;
                yield 7.0F + sum;
            }
            case NIGHTMARE -> {
                float f = (((float) dayTime - 72000.0F) / 888000.0F) + 1;
                float f1 = Mth.clamp(((float) dayTime - 72000.0F) / 1728000.0F, 0.0F, 2);
                float f2 = Mth.clamp((float) chunkTime / 1440000.0F, 0.0F, 2);
                float f3 = Mth.clamp(moonBrightness * f, 0.0F, f);
                float sum = f1 + f2 + f3;
                yield 10.0F + sum;
            }
            default -> {
                boolean flag = difficultyGeneral == DifficultyGeneral.HARD;
                float f = 0.75F;
                float f1 = Mth.clamp(((float) dayTime + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
                f += f1;
                float f2 = 0.0F;
                f2 += Mth.clamp((float) chunkTime / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
                f2 += Mth.clamp(moonBrightness * 0.25F, 0.0F, f1);
                if (difficultyGeneral == DifficultyGeneral.EASY) {
                    f2 *= 0.5F;
                }

                f += f2;
                yield (float) p_19052_.getId() * f;
            }
        };
    }

    @Override
    @Unique
    public void preCalculateDifficulty(Level level) {
        difficultyGeneral = ((ILevel) (Object) level).getDifficultyGen();
        if (difficultyGeneral == null) {
            throw new NullPointerException("difficultyGeneral is null");
        }
        effectiveDifficulty = calculateDifficulty(level.getDifficulty(), dayTime, chunkTime, moonPhase);
    }

    @Unique
    @Override
    public DifficultyGeneral getDifficultyGen() {
        return difficultyGeneral;
    }

}
