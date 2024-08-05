package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

import static net.minecraft.world.entity.Mob.getEquipmentForSlot;

@Mixin(Mob.class)
public abstract class MixMob extends LivingEntity {

    public MixMob(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
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
    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        if (p_217055_.nextFloat() < 0.15F * p_217056_.getSpecialMultiplier()) {
            DifficultyGeneral difficultyGen = ((ILevel) level()).getDifficultyGen();
            float j = switch (difficultyGen) {
                case INSANE -> 0.05F;
                case NIGHTMARE -> 0.1F;
                default -> 0;
            };
            int i = p_217055_.nextInt(2);
            float f = switch (difficultyGen) {
                case HARD -> 0.1F;
                case INSANE -> 0.05F;
                case NIGHTMARE -> 0.025F;
                default -> 0.25F;
            };
            if (p_217055_.nextFloat() < 0.095F + j) {
                i++;
            }

            if (p_217055_.nextFloat() < 0.095F + j) {
                i++;
            }

            if (p_217055_.nextFloat() < 0.095F + j) {
                i++;
            }

            boolean flag = true;

            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                if (equipmentslot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemstack = this.getItemBySlot(equipmentslot);
                    if (!flag && p_217055_.nextFloat() < f) {
                        break;
                    }

                    flag = false;
                    if (itemstack.isEmpty()) {
                        Item item = getEquipmentForSlot(equipmentslot, i);
                        if (item != null) {
                            this.setItemSlot(equipmentslot, new ItemStack(item));
                        }
                    }
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void enchantSpawnedEquipment(ServerLevelAccessor p_342440_, EquipmentSlot p_344135_, RandomSource p_344290_, float p_343248_, DifficultyInstance p_345046_) {
        ItemStack itemstack = this.getItemBySlot(p_344135_);
        DifficultyGeneral difficultyGen = ((ILevel) level()).getDifficultyGen();
        if (!itemstack.isEmpty() && p_344290_.nextFloat() < p_343248_ * p_345046_.getSpecialMultiplier()) {
            HolderGetter<Enchantment> holdergetter = p_342440_.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();
            int i = switch (difficultyGen) {case INSANE -> 7;case NIGHTMARE -> 18;default -> 0;};
            EnchantmentProvider enchantmentProvider = new EnchantmentsByCostWithDifficulty(holdergetter.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5 + i, 17 + i);
            EnchantmentHelper.updateEnchantments(itemstack, p_341687_ -> enchantmentProvider.enchant(itemstack, p_341687_, random, p_345046_));
            this.setItemSlot(p_344135_, itemstack);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);
            DifficultyGeneral difficult = ((ILevel) level()).getDifficultyGen();
            int j = 0;
            switch (difficult) {
                case EASY -> j = 8;
                case NORMAL -> j = 4;
                case HARD -> j = 0;
                case INSANE -> j = -2;
                case NIGHTMARE -> j = -5;
            }
            i -= j;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }
}
