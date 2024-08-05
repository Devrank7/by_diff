package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Piglin.class)
public class MixPiglin extends AbstractPiglin {

    public MixPiglin(EntityType<? extends AbstractPiglin> p_34652_, Level p_34653_) {
        super(p_34652_, p_34653_);
    }

    @Override
    @Shadow
    protected boolean canHunt() {
        return false;
    }

    @Override
    @Shadow
    public PiglinArmPose getArmPose() {
        return null;
    }

    @Override
    @Shadow
    protected void playConvertedSound() {

    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void maybeWearArmor(EquipmentSlot p_219192_, ItemStack p_219193_, RandomSource p_219194_) {
        DifficultyInstance difficultyInstance = level().getCurrentDifficultyAt(this.blockPosition());
        DifficultyGeneral difficultyGeneral = ((IDifficultyInstance)difficultyInstance).getDifficultyGen();
        boolean flag = difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
        float f = difficultyInstance.getSpecialMultiplier() * 0.2f + 0.05f;
        if (p_219194_.nextFloat() < (flag ? f: 0.1F)) {
            this.setItemSlot(p_219192_, p_219193_);
        }
    }
}
