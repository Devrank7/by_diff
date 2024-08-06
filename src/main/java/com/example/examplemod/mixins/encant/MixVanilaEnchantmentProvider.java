package com.example.examplemod.mixins.encant;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VanillaEnchantmentProviders.class)
public interface MixVanilaEnchantmentProvider {
    @Shadow
    ResourceKey<EnchantmentProvider> MOB_SPAWN_EQUIPMENT = create("mob_spawn_equipment");
    @Shadow
    ResourceKey<EnchantmentProvider> PILLAGER_SPAWN_CROSSBOW = create("pillager_spawn_crossbow");
    @Shadow
    ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_3 = create("raid/pillager_post_wave_3");
    @Shadow
    ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_5 = create("raid/pillager_post_wave_5");
    @Shadow
    ResourceKey<EnchantmentProvider> RAID_VINDICATOR = create("raid/vindicator");
    @Shadow
    ResourceKey<EnchantmentProvider> RAID_VINDICATOR_POST_WAVE_5 = create("raid/vindicator_post_wave_5");
    @Shadow
    ResourceKey<EnchantmentProvider> ENDERMAN_LOOT_DROP = create("enderman_loot_drop");

    /**
     * @author
     * @reason
     */
    @Overwrite
    static void bootstrap(BootstrapContext<EnchantmentProvider> p_344835_) {
        HolderGetter<Enchantment> holdergetter = p_344835_.lookup(Registries.ENCHANTMENT);
        p_344835_.register(MOB_SPAWN_EQUIPMENT, new EnchantmentsByCostWithDifficulty(holdergetter.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 30, 40));
        p_344835_.register(PILLAGER_SPAWN_CROSSBOW, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.PIERCING), ConstantInt.of(1)));
        p_344835_.register(RAID_PILLAGER_POST_WAVE_3, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(1)));
        p_344835_.register(RAID_PILLAGER_POST_WAVE_5, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(2)));
        p_344835_.register(RAID_VINDICATOR, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(1)));
        p_344835_.register(RAID_VINDICATOR_POST_WAVE_5, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(2)));
        p_344835_.register(ENDERMAN_LOOT_DROP, new SingleEnchantment(holdergetter.getOrThrow(Enchantments.SILK_TOUCH), ConstantInt.of(1)));
    }

    @Shadow
    static ResourceKey<EnchantmentProvider> create(String p_342501_) {
        return ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.withDefaultNamespace(p_342501_));
    }
}
