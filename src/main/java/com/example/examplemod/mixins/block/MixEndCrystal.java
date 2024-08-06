package com.example.examplemod.mixins.block;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndCrystal.class)
public class MixEndCrystal extends Entity {
    public MixEndCrystal(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    @Shadow
    protected void defineSynchedData(SynchedEntityData.Builder p_333664_) {

    }

    @Override
    @Shadow
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    @Shadow
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    @Shadow
    private void onDestroyedBy(DamageSource p_31048_) {
    }

    @Override
    public boolean hurt(DamageSource p_31050_, float p_31051_) {
        DifficultyGeneral difficultyGeneral = ((ILevel) this.level()).getDifficultyGen();
        boolean flag = (difficultyGeneral == DifficultyGeneral.INSANE && random.nextBoolean()) || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
        boolean flag1 = p_31050_.getDirectEntity() instanceof AbstractArrow;
        if (this.isInvulnerableTo(p_31050_)) {
            return false;
        } else if (p_31050_.getEntity() instanceof EnderDragon) {
            return false;
        } else if (flag && flag1) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                this.remove(Entity.RemovalReason.KILLED);
                if (!p_31050_.is(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource damagesource = p_31050_.getEntity() != null ? this.damageSources().explosion(this, p_31050_.getEntity()) : null;
                    this.level()
                            .explode(this, damagesource, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                }

                this.onDestroyedBy(p_31050_);
            }

            return true;
        }
    }
}
