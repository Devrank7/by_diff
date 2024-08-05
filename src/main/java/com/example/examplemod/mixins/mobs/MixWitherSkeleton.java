package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WitherSkeleton.class)
public class MixWitherSkeleton extends AbstractSkeleton {
    public MixWitherSkeleton(EntityType<? extends AbstractSkeleton> p_32133_, Level p_32134_) {
        super(p_32133_, p_32134_);
    }

    @Override
    @Shadow
    protected SoundEvent getStepSound() {
        return null;
    }

    @Override
    public boolean doHurtTarget(Entity p_34169_) {
        if (!super.doHurtTarget(p_34169_)) {
            return false;
        } else {
            if (p_34169_ instanceof LivingEntity) {
                DifficultyGeneral difficultyGeneral = ((ILevel) (Object) level()).getDifficultyGen();
                int force = switch (difficultyGeneral) {
                    case NIGHTMARE -> {
                        if (random.nextInt(10) == 0) {
                            yield 2;
                        } else if (random.nextInt(10) < 4) {
                            yield 1;
                        }
                        yield 0;
                    }
                    default -> 0;
                };
                int duration = switch (difficultyGeneral) {
                    case INSANE -> 8 * 20;
                    case NIGHTMARE -> 15 * 20;
                    default -> 0;
                };
                ((LivingEntity) p_34169_).addEffect(new MobEffectInstance(MobEffects.WITHER, 200 + duration, force), this);
            }

            return true;
        }
    }
}
