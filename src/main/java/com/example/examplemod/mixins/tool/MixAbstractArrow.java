package com.example.examplemod.mixins.tool;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractArrow.class)
public class MixAbstractArrow extends Projectile {

    public MixAbstractArrow(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Shadow
    public void setBaseDamage(double p_36782_) {
    }

    @Override
    @Shadow
    protected void defineSynchedData(SynchedEntityData.Builder p_333664_) {

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setBaseDamageFromMob(float p_345045_) {
        this.setBaseDamage((double) (p_345045_ * 2.0F) + this.random.triangle((double) this.level().getDifficulty().getId() * 0.11, 0.57425));
    }
}
