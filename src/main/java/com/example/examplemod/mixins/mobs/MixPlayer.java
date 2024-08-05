package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class MixPlayer extends LivingEntity {

    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    protected abstract void removeEntitiesOnShoulder();

    public MixPlayer(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    public boolean hurt(DamageSource p_36154_, float p_36155_) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, p_36154_, p_36155_)) return false;
        if (this.isInvulnerableTo(p_36154_)) {
            return false;
        } else if (this.abilities.invulnerable && !p_36154_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                if (!this.level().isClientSide) {
                    this.removeEntitiesOnShoulder();
                }
                DifficultyGeneral my_difficult = ((ILevel) level()).getDifficultyGen();

                if (p_36154_.scalesWithDifficulty()) {
                    if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                        p_36155_ = 0.0F;
                    }

                    if (my_difficult == DifficultyGeneral.EASY) {
                        p_36155_ = Math.min(p_36155_ / 2.0F + 1.0F, p_36155_);
                    }

                    if (my_difficult == DifficultyGeneral.HARD) {
                        p_36155_ = p_36155_ * 3.0F / 2.0F;
                    }
                    if (my_difficult == DifficultyGeneral.INSANE) {
                        p_36155_ = p_36155_ * 2.0F;
                    }
                    if (my_difficult == DifficultyGeneral.NIGHTMARE) {
                        p_36155_ = p_36155_ * 3.0F;
                    }
                }

                return p_36155_ == 0.0F ? false : super.hurt(p_36154_, p_36155_);
            }
        }
    }

    @Override
    @Shadow
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    @Shadow
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return null;
    }

    @Override
    @Shadow
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {

    }

    @Override
    @Shadow
    public HumanoidArm getMainArm() {
        return null;
    }


}
