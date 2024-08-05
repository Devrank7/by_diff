package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(CrossbowAttackMob.class)
public interface MixCrossbowAttackMob {

    @Shadow
    void onCrossbowAttackPerformed();

    @Nullable
    @Shadow
    LivingEntity getTarget();

    /**
     * @author
     * @reason
     */
    @Overwrite
    default void performCrossbowAttack(LivingEntity p_32337_, float p_32338_) {
        InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(p_32337_, item -> item instanceof CrossbowItem);
        ItemStack itemstack = p_32337_.getItemInHand(interactionhand);
        if (p_32337_.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
            DifficultyGeneral difficult = ((ILevel) p_32337_.level()).getDifficultyGen();
            int i = switch (difficult) {
                case EASY -> 4;
                case NORMAL -> 8;
                case HARD -> 12;
                case INSANE, NIGHTMARE -> 14;
                default -> 0;
            };
            var crossbowitem = (CrossbowItem) itemstack.getItem();
            crossbowitem.performShooting(
                    p_32337_.level(), p_32337_, interactionhand, itemstack, p_32338_, (float) (14 - i), this.getTarget()
            );
        }

        this.onCrossbowAttackPerformed();
    }
}
