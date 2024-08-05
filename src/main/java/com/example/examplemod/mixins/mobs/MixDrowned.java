package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Drowned.class)
public class MixDrowned extends Monster {

    public MixDrowned(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void performRangedAttack(LivingEntity p_32356_, float p_32357_) {
        ThrownTrident throwntrident = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
        double d0 = p_32356_.getX() - this.getX();
        double d1 = p_32356_.getY(0.3333333333333333D) - throwntrident.getY();
        double d2 = p_32356_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        DifficultyGeneral difficultyGeneral = ((ILevel) level()).getDifficultyGen();
        int i = switch (difficultyGeneral) {
            case EASY -> 4;
            case NORMAL -> 8;
            case HARD -> 12;
            case INSANE,NIGHTMARE -> 14;
            default -> 0;
        };
        throwntrident.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - i));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(throwntrident);
    }
}
