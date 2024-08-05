package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Vex.class)
public class MixVex extends Monster {

    public MixVex(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
        DifficultyGeneral lev_diff = ((ILevel) level()).getDifficultyGen();
        Item item = switch (lev_diff) {
            case INSANE -> {
                if (random.nextFloat() < 0.2f) {
                    yield Items.DIAMOND_SWORD;
                }
                yield Items.IRON_SWORD;
            }
            case NIGHTMARE -> {
                if (random.nextFloat() < 0.4f) {
                    yield Items.DIAMOND_SWORD;
                }
                yield Items.IRON_SWORD;
            }
            default -> Items.IRON_AXE;
        };
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(item));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
}
