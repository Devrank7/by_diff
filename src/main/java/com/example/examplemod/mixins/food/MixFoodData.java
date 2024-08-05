package com.example.examplemod.mixins.food;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FoodData.class)
public abstract class MixFoodData {

    @Shadow
    private int lastFoodLevel;

    @Shadow
    private int foodLevel;

    @Shadow
    private float exhaustionLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private int tickTimer;

    @Shadow
    public abstract void addExhaustion(float p_38704_);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick(Player p_38711_) {
        Difficulty difficulty = p_38711_.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean flag = p_38711_.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && this.saturationLevel > 0.0F && p_38711_.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                p_38711_.heal(f / 6.0F);
                this.addExhaustion(f);
                this.tickTimer = 0;
            }
        } else if (flag && this.foodLevel >= 18 && p_38711_.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                p_38711_.heal(1.0F);
                this.addExhaustion(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                DifficultyGeneral my_difficult = ((ILevel) p_38711_.level()).getDifficultyGen();
                if (p_38711_.getHealth() > 10.0F || my_difficult == DifficultyGeneral.HARD || my_difficult == DifficultyGeneral.INSANE || my_difficult == DifficultyGeneral.NIGHTMARE || (p_38711_.getHealth() > 1.0F && my_difficult == DifficultyGeneral.NORMAL)) {
                    float i = 0;
                    if (my_difficult == DifficultyGeneral.NIGHTMARE) i+=2;
                    if (my_difficult == DifficultyGeneral.INSANE) i++;
                    p_38711_.hurt(p_38711_.damageSources().starve(), 1.0F + i);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }

    }
}
