package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.google.common.collect.Maps;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(Pillager.class)
public class MixPilager extends AbstractIllager {

    public MixPilager(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public void applyRaidBuffs(ServerLevel p_343389_, int p_37844_, boolean p_37845_) {
        Raid raid = this.getCurrentRaid();
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            EnchantmentProvider enchantmentProvider;
            int i = switch (((ILevel) level()).getDifficultyGen()) {
                case INSANE -> 1;
                case NIGHTMARE -> random.nextBoolean() ? 3 : 2;
                default -> 0;
            };
            HolderGetter<Enchantment> holdergetter = p_343389_.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();
            if (p_37844_ > raid.getNumGroups(Difficulty.NORMAL)) {
                enchantmentProvider = new SingleEnchantment(holdergetter.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(2 + i));
            } else if (p_37844_ > raid.getNumGroups(Difficulty.EASY)) {
                enchantmentProvider = new SingleEnchantment(holdergetter.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(1 + i));
            } else {
                enchantmentProvider = null;
            }

            if (enchantmentProvider != null) {
                EnchantmentHelper.updateEnchantments(itemstack, p_341687_ -> enchantmentProvider.enchant(itemstack, p_341687_, random, p_343389_.getCurrentDifficultyAt(this.blockPosition())));
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    protected void enchantSpawnedWeapon(ServerLevelAccessor p_343786_, RandomSource p_219056_, DifficultyInstance p_344265_) {
        super.enchantSpawnedWeapon(p_343786_, p_219056_, p_344265_);
        if (p_219056_.nextInt(300) == 0) {
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.is(Items.CROSSBOW)) {
                HolderGetter<Enchantment> holdergetter = p_343786_.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();
                int i = switch (((ILevel) level()).getDifficultyGen()) {
                    case INSANE -> 1;
                    case NIGHTMARE -> random.nextBoolean() ? 3 : 2;
                    default -> 0;
                };
                EnchantmentProvider enchantmentProvider = new SingleEnchantment(holdergetter.getOrThrow(Enchantments.PIERCING), ConstantInt.of(1 + i));
                EnchantmentHelper.updateEnchantments(itemstack, p_341687_ -> enchantmentProvider.enchant(itemstack, p_341687_, random, p_343786_.getCurrentDifficultyAt(this.blockPosition())));
            }
        }
    }

    @Override
    @Shadow
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
