package com.example.examplemod.mixins.mobs.goal;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SkeletonTrapGoal.class)
public class MixSkeletonTrapGoal {
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void enchant(Skeleton p_344708_, EquipmentSlot p_342622_, DifficultyInstance p_343379_) {
        ItemStack itemstack = p_344708_.getItemBySlot(p_342622_);
        itemstack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        DifficultyGeneral difficultyGen = ((ILevel) p_344708_.level()).getDifficultyGen();
        HolderGetter<Enchantment> holdergetter = p_344708_.level().registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();
        int i = switch (difficultyGen) {case INSANE -> 7;case NIGHTMARE -> 18;default -> 0;};
        EnchantmentProvider enchantmentProvider = new EnchantmentsByCostWithDifficulty(holdergetter.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5 + i, 17 + i);
        EnchantmentHelper.updateEnchantments(itemstack, p_341687_ -> enchantmentProvider.enchant(itemstack, p_341687_, p_344708_.getRandom(), p_343379_));
        p_344708_.setItemSlot(p_342622_, itemstack);
    }
}
