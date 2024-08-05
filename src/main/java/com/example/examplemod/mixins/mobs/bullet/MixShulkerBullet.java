package com.example.examplemod.mixins.mobs.bullet;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.google.common.base.MoreObjects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBullet.class)
public class MixShulkerBullet extends Projectile {

    public MixShulkerBullet(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Override
    @Shadow
    protected void defineSynchedData(SynchedEntityData.Builder p_333664_) {

    }

    @Override
    protected void onHitEntity(EntityHitResult p_37345_) {
        super.onHitEntity(p_37345_);
        Entity entity = p_37345_.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity)entity1 : null;
        DamageSource damagesource = this.damageSources().mobProjectile(this, livingentity);
        boolean flag = entity.hurt(damagesource, 4.0F);
        if (flag) {
            if (this.level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity, damagesource);
            }

            if (entity instanceof LivingEntity livingentity1) {
                DifficultyGeneral difficultygeneral = ((ILevel) this.level()).getDifficultyGen();
                boolean flag1 = difficultygeneral == DifficultyGeneral.INSANE || difficultygeneral == DifficultyGeneral.NIGHTMARE;
                int i;
                int j;
                switch (difficultygeneral) {
                    case INSANE -> {
                        i = random.nextInt(2,6) * 20;
                        j = random.nextInt(5) == 0 ? 1 : 0;
                    }
                    case NIGHTMARE -> {
                        i = random.nextInt(6, 11) * 20;
                        j = random.nextBoolean() ? 1 : 0;
                        j = random.nextInt(10) == 0 ? 2 : j;
                    }
                    default -> {
                        i = 0;
                        j = 0;
                    }
                }
                livingentity1.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200 + i, j), MoreObjects.firstNonNull(entity1, this));
            }
        }
    }
}
