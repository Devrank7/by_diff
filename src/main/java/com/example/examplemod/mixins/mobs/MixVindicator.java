package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vindicator.class)
public class MixVindicator extends AbstractIllager {
    public MixVindicator(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
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
    public void applyRaidBuffs(ServerLevel p_343632_, int p_34079_, boolean p_34080_) {
        DifficultyGeneral difficultygeneral = ((ILevel) level()).getDifficultyGen();
        Item item = switch (difficultygeneral) {
            case INSANE -> {
                if (random.nextFloat() < 0.2f) {
                    yield Items.DIAMOND_AXE;
                }
                yield Items.IRON_AXE;
            }
            case NIGHTMARE -> {
                if (random.nextFloat() < 0.4f) {
                    yield Items.DIAMOND_AXE;
                }
                yield Items.IRON_AXE;
            }
            default -> Items.IRON_AXE;
        };
        ItemStack itemstack = new ItemStack(item);
        Raid raid = this.getCurrentRaid();
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            HolderGetter<Enchantment> holdergetter = p_343632_.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();
            int i = switch (((ILevel) level()).getDifficultyGen()) {
                case INSANE -> 1;
                case NIGHTMARE -> random.nextBoolean() ? 3 : 2;
                default -> 0;
            };
            EnchantmentProvider resourcekey = new SingleEnchantment(holdergetter.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of((p_34079_ > raid.getNumGroups(Difficulty.NORMAL) ? 2 : 1) + i));
            EnchantmentHelper.updateEnchantments(itemstack, p_341687_ -> resourcekey.enchant(itemstack, p_341687_, random, p_343632_.getCurrentDifficultyAt(this.blockPosition())));
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
    }
}
