package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(HoglinBase.class)
public interface MixHoglinBase {
    /**
     * @author
     * @reason
     */
    @Overwrite
    static void throwTarget(LivingEntity p_34646_, LivingEntity p_34647_) {
        DifficultyGeneral difficultyGeneral = ((ILevel) (Object) p_34646_.level()).getDifficultyGen();
        double d = switch (difficultyGeneral) {
            case INSANE -> 1.8f;
            case NIGHTMARE -> 2.4f;
            default -> 1.0f;
        };
        double d0 = p_34646_.getAttributeValue(Attributes.ATTACK_KNOCKBACK) * d;
        double d1 = p_34647_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double d2 = d0 - d1;
        if (!(d2 <= 0.0)) {
            double d3 = p_34647_.getX() - p_34646_.getX();
            double d4 = p_34647_.getZ() - p_34646_.getZ();
            float f = (float) (p_34646_.level().random.nextInt(21) - 10);
            double d5 = d2 * (double) (p_34646_.level().random.nextFloat() * 0.5F + 0.2F);
            Vec3 vec3 = new Vec3(d3, 0.0, d4).normalize().scale(d5).yRot(f);
            double d6 = d2 * (double) p_34646_.level().random.nextFloat() * 0.5;
            p_34647_.push(vec3.x, d6, vec3.z);
            p_34647_.hurtMarked = true;
        }
    }
}
