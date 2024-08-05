package com.example.examplemod.mixins;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixItem {

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void useOn(Level p_41432_, Player p_41433_, InteractionHand p_41434_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
        System.err.println("USE");
    }
}
