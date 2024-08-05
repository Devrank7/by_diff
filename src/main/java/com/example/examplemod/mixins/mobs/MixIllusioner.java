package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Illusioner.class)
public class MixIllusioner extends SpellcasterIllager implements RangedAttackMob {
    public MixIllusioner(EntityType<? extends SpellcasterIllager> p_33724_, Level p_33725_) {
        super(p_33724_, p_33725_);
    }

    @Override
    @Shadow
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    @Shadow
    public void applyRaidBuffs(ServerLevel p_343389_, int p_37844_, boolean p_37845_) {

    }

    @Override
    @Shadow
    public SoundEvent getCelebrateSound() {
        return null;
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public void performRangedAttack(LivingEntity p_32918_, float p_32919_) {
        ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
        ItemStack itemstack1 = this.getProjectile(itemstack);
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack1, p_32919_, itemstack);
        if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem bow) {
            abstractarrow = bow.customArrow(abstractarrow);
        }
        double d0 = p_32918_.getX() - this.getX();
        double d1 = p_32918_.getY(0.3333333333333333) - abstractarrow.getY();
        double d2 = p_32918_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        DifficultyGeneral difficult = ((ILevel) level()).getDifficultyGen();
        int i = switch (difficult) {
            case EASY -> 4;
            case NORMAL -> 8;
            case HARD -> 12;
            case INSANE, NIGHTMARE -> 14;
            default -> 0;
        };
        abstractarrow.shoot(d0, d1 + d3 * 0.2F, d2, 1.6F, (float) (14 - i));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }
}
