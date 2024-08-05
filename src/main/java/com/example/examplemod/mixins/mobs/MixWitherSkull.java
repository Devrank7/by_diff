package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WitherSkull.class)
public class MixWitherSkull extends AbstractHurtingProjectile {

    public MixWitherSkull(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public MixWitherSkull(EntityType<? extends AbstractHurtingProjectile> p_310629_, double p_311590_, double p_312782_, double p_309484_, Level p_311660_) {
        super(p_310629_, p_311590_, p_312782_, p_309484_, p_311660_);
    }

    public MixWitherSkull(EntityType<? extends AbstractHurtingProjectile> p_36817_, double p_36818_, double p_36819_, double p_36820_, Vec3 p_343716_, Level p_36824_) {
        super(p_36817_, p_36818_, p_36819_, p_36820_, p_343716_, p_36824_);
    }

    public MixWitherSkull(EntityType<? extends AbstractHurtingProjectile> p_36826_, LivingEntity p_36827_, Vec3 p_343596_, Level p_36831_) {
        super(p_36826_, p_36827_, p_343596_, p_36831_);
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level().isClientSide) {
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity1;
                DamageSource damagesource = this.damageSources().witherSkull((WitherSkull) (Object) this, livingentity);
                flag = entity.hurt(damagesource, 8.0F);
                if (flag) {
                    if (entity.isAlive()) {
                        EnchantmentHelper.doPostAttackEffects((ServerLevel) level(), entity, damagesource);
                    } else {
                        livingentity.heal(5.0F);
                    }
                }
            } else {
                flag = entity.hurt(this.damageSources().magic(), 5.0F);
            }

            if (flag && entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                DifficultyGeneral my_difficult = ((ILevel) this.level()).getDifficultyGen();
                int i = 0;
                int j = 0;
                switch (my_difficult) {
                    case NORMAL -> i = 10;
                    case HARD -> i = 40;
                    case INSANE -> {
                        i = 50;
                        j = 1;
                    }
                    case NIGHTMARE -> {
                        i = 75;
                        j = 2;
                    }
                }

                if (i > 0) {
                    livingentity1.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 1 + j), this.getEffectSource());
                }
            }

        }
    }
}
