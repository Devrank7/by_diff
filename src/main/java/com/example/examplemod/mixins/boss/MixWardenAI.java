package com.example.examplemod.mixins.boss;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WardenAi.class)
public class MixWardenAI {
    private static int callback = 18;
    private static float speed = 1.2f;

    @Shadow
    private static boolean isTarget(Warden p_219515_, LivingEntity p_219516_) {
        return true;
    }

    @Shadow
    @Final
    private static BehaviorControl<Warden> DIG_COOLDOWN_SETTER;

    @Shadow
    private static void onTargetInvalid(Warden p_219529_, LivingEntity p_219530_) {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static void initFightActivity(Warden p_219518_, Brain<Warden> p_219519_) {
        if (isServer(p_219518_.level())) {
            DifficultyGeneral difficultyGen = ((ILevel) p_219518_.level()).getDifficultyGen();
            switch (difficultyGen) {
                case INSANE -> {
                    callback = 13;
                    speed = 1.5f;
                }
                case NIGHTMARE -> {
                    callback = 10;
                    speed = 1.8f;
                }
            }
        }
        p_219519_.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                DIG_COOLDOWN_SETTER,
                StopAttackingIfTargetInvalid.<Warden>create((p_219540_) -> {
                    return !p_219518_.getAngerLevel().isAngry() || !p_219518_.canTargetEntity(p_219540_);
                }, MixWardenAI::onTargetInvalid, false),
                SetEntityLookTarget.create((p_219535_) -> {
                    return isTarget(p_219518_, p_219535_);
                }, (float) p_219518_.getAttributeValue(Attributes.FOLLOW_RANGE)),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(speed),
                new SonicBoom(),
                MeleeAttack.create(callback)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isServer(Level level) {
        return !level.isClientSide;
    }
}
