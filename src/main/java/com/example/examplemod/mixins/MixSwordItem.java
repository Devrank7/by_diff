package com.example.examplemod.mixins;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class MixSwordItem extends TieredItem {
    public MixSwordItem(Tier p_43308_, Properties p_43309_) {
        super(p_43308_, p_43309_);
    }

    /**
     * @author Devlink
     * @reason hurtEnemy
     */
    @Inject(at = @At("HEAD"), method = "hurtEnemy", remap = false)
    public void hurtEnemy(ItemStack p_43278_, LivingEntity p_43279_, LivingEntity p_43280_, CallbackInfoReturnable<Boolean> ci) {
        System.err.println("item = " + p_43278_.getItem().toString() + " entity = " + p_43279_.toString() + " target = " + p_43280_.toString());
    }

    @Inject(at = @At("RETURN"), method = "canPerformAction", remap = false, cancellable = true)
    public void canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction, CallbackInfoReturnable<Boolean> cir) {
        System.err.println("SOME = " + cir.getReturnValue());
        cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "postHurtEnemy", remap = false, cancellable = true)
    public void postHurtEnemy(ItemStack p_342189_, LivingEntity p_344347_, LivingEntity p_343888_, CallbackInfo ci) {
        ci.cancel();
        p_342189_.hurtAndBreak(100, p_343888_, EquipmentSlot.MAINHAND);
        System.err.println("item = " + p_342189_.getItem().toString() + " entity = " + p_344347_.toString() + " target = " + p_343888_.toString());
    }

}
