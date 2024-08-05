package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Bee.class)
public abstract class MixBee extends Animal implements NeutralMob, FlyingAnimal {

    public MixBee(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Shadow
    protected abstract void setHasStung(boolean p_27926_);

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public boolean doHurtTarget(Entity p_27722_) {
        DamageSource damagesource = this.damageSources().sting(this);
        boolean flag = p_27722_.hurt(damagesource, (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            if (this.level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, p_27722_, damagesource);
            }

            if (p_27722_ instanceof LivingEntity livingentity) {
                livingentity.setStingerCount(livingentity.getStingerCount() + 1);
                DifficultyGeneral difficultyGeneral = ((ILevel) level()).getDifficultyGen();
                int i = switch (difficultyGeneral) {
                    case NORMAL -> 10;
                    case HARD -> 18;
                    case INSANE -> 30;
                    case NIGHTMARE -> 45;
                    default -> 0;
                };
                if (i > 0) {
                    livingentity.addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0), this);
                }
            }

            this.setHasStung(true);
            this.stopBeingAngry();
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    @Shadow
    public boolean isFood(ItemStack p_27600_) {
        return false;
    }
}
