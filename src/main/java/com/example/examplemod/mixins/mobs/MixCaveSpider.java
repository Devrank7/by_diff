package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CaveSpider.class)
public class MixCaveSpider extends Spider {

    public MixCaveSpider(EntityType<? extends Spider> p_33786_, Level p_33787_) {
        super(p_33786_, p_33787_);
    }

    public boolean doHurtTarget(Entity p_32257_) {
        if (super.doHurtTarget(p_32257_)) {
            DifficultyGeneral lev_diff = ((ILevel) level()).getDifficultyGen();
            if (p_32257_ instanceof LivingEntity) {
                int i = switch (lev_diff) {
                    case NORMAL -> 7;
                    case HARD -> 15;
                    case INSANE -> 30;
                    case NIGHTMARE -> 35;
                    default -> 0;
                };
                if (i > 0) {
                    ((LivingEntity)p_32257_).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, lev_diff == DifficultyGeneral.NIGHTMARE ? 1 : 0), this);
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
