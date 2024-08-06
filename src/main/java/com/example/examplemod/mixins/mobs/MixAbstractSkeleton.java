package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.goal.ShootAtGoal;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(AbstractSkeleton.class)
public abstract class MixAbstractSkeleton extends Monster {
    @Shadow
    @Final
    private MeleeAttackGoal meleeGoal;

    @Shadow
    @Final
    private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    private ShootAtGoal shootAtGoal = new ShootAtGoal(((AbstractSkeleton) (Object) this), TransparentBlock.class, 50, 10, 1.0f, 20, 10.0F);

    @Shadow
    protected abstract AbstractArrow getArrow(ItemStack p_32156_, float p_32157_, @Nullable ItemStack p_343583_);

    public MixAbstractSkeleton(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void reassessWeaponGoal() {
        if (level() != null && !level().isClientSide) {
            this.goalSelector.removeGoal(meleeGoal);
            this.goalSelector.removeGoal(bowGoal);
            this.goalSelector.removeGoal(shootAtGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
            if (itemstack.is(Items.BOW)) {
                DifficultyGeneral lev_diff = ((ILevel) level()).getDifficultyGen();
                int i = switch (lev_diff) {
                    case HARD -> 20;
                    case INSANE -> 12;
                    case NIGHTMARE -> 7;
                    default -> 40;
                };
                this.bowGoal.setMinAttackInterval(i);
                shootAtGoal.setAttackIntervalMin(i);
                DifficultyGeneral difficultyGeneral = ((ILevel) level()).getDifficultyGen();
                boolean flag = difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
                boolean flag1 = random.nextFloat() < (DifficultyGeneral.NIGHTMARE == difficultyGeneral ? 0.4F : 0.2F);
                if (flag && flag1) {
                    this.goalSelector.addGoal(4, shootAtGoal);
                }
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void performRangedAttack(LivingEntity p_32141_, float p_32142_) {
        ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
        ItemStack itemstack1 = this.getProjectile(itemstack);
        AbstractArrow abstractarrow = this.getArrow(itemstack1, p_32142_, itemstack);
        if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
            abstractarrow = ((net.minecraft.world.item.BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrow);
        double d0 = p_32141_.getX() - this.getX();
        double d1 = p_32141_.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = p_32141_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        DifficultyGeneral lev_diff = ((ILevel) level()).getDifficultyGen();
        int i = switch (lev_diff) {
            case EASY -> 4;
            case NORMAL -> 8;
            case HARD -> 12;
            case INSANE, NIGHTMARE -> 14;
            default -> 0;
        };
        abstractarrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - i));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }
}
