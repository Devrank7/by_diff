package com.example.examplemod.difficulty.effect;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.material.LavaFluid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEffectUtils {


    private static List<Holder<MobEffect>> meleeAttackEffects = List.of(MobEffects.CONFUSION, MobEffects.BLINDNESS, MobEffects.WEAKNESS, MobEffects.HUNGER);
    private static List<Holder<MobEffect>> rangedAttackEffects = List.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.POISON, MobEffects.BLINDNESS, MobEffects.DIG_SLOWDOWN);
    private static List<Holder<MobEffect>> guardianAttackEffects = List.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.BLINDNESS, MobEffects.WEAKNESS, MobEffects.CONFUSION);
    private static List<Holder<MobEffect>> defaultBufEffect = List.of(MobEffects.INFESTED,MobEffects.DAMAGE_RESISTANCE);
    private static List<Holder<MobEffect>> rangedBufEffect = List.of(MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_RESISTANCE);

    public static <T extends Mob & Enemy> EffectData getRandomEffectForType(T type, DifficultyInstance difficulty, RandomSource random) {
        return switch (type) {
            case Spider s -> spiderEffect(random, difficulty, s);
            case Piglin piglin -> {
                if (piglin.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == Items.GOLDEN_SWORD) {
                    yield EffectTo(random, difficulty, piglin, meleeAttackEffects, defaultBufEffect);
                }
                yield EffectTo(random, difficulty, piglin, rangedAttackEffects, rangedBufEffect);
            }
            case EnderMan s -> EffectTo(random, difficulty, type, meleeAttackEffects, defaultBufEffect);
            case Blaze s -> {
                List<Holder<MobEffect>> list = new ArrayList<>(guardianAttackEffects);
                list.add(MobEffects.REGENERATION);
                yield EffectTo(random, difficulty, type, guardianAttackEffects,list);
            }
            case Guardian s -> {
                List<Holder<MobEffect>> list = new ArrayList<>();
                list.add(MobEffects.REGENERATION);
                list.add(MobEffects.DAMAGE_RESISTANCE);
                list.add(MobEffects.MOVEMENT_SPEED);
                yield EffectTo(random, difficulty, type, guardianAttackEffects, list);
            }
            case Pillager s -> EffectTo(random, difficulty, type, rangedAttackEffects, rangedBufEffect);
            case Vindicator s -> EffectTo(random, difficulty, type, meleeAttackEffects, rangedBufEffect);
            case Evoker s -> EffectTo(random, difficulty, type, guardianAttackEffects, rangedBufEffect);
            case HoglinBase s -> EffectTo(random, difficulty, type, meleeAttackEffects, List.of(MobEffects.DAMAGE_RESISTANCE));
            case Breeze s -> {
                List<Holder<MobEffect>> list = new ArrayList<>(guardianAttackEffects);
                list.add(MobEffects.LEVITATION);
                yield EffectTo(random, difficulty, type, list, List.of(MobEffects.OOZING,MobEffects.DAMAGE_RESISTANCE,MobEffects.INFESTED,MobEffects.WIND_CHARGED));
            }
            case Slime s -> EffectTo(random, difficulty, type, meleeAttackEffects, List.of(MobEffects.DAMAGE_RESISTANCE,MobEffects.OOZING));
            case Phantom s -> EffectTo(random, difficulty, type, meleeAttackEffects, defaultBufEffect);
            case Ravager s -> EffectTo(random, difficulty, type, meleeAttackEffects, defaultBufEffect);
            case AbstractSkeleton s when s.getType() != EntityType.WITHER_SKELETON -> EffectTo(random, difficulty, type, rangedAttackEffects, rangedAttackEffects);
            case WitherSkeleton sf -> EffectTo(random, difficulty, type, rangedAttackEffects, List.of(MobEffects.DAMAGE_RESISTANCE,MobEffects.MOVEMENT_SPEED));
            case Vex sf -> EffectTo(random, difficulty, type, meleeAttackEffects, defaultBufEffect);
            case Warden sf -> EffectTo(random, difficulty, type, meleeAttackEffects, List.of(MobEffects.INFESTED));
            case Zombie sf -> EffectTo(random, difficulty, type, meleeAttackEffects, defaultBufEffect);
            default -> {
                System.out.println("Unknown mob type: " + type);
                yield null;
            }
        };
    }

    // position, blindness, nausea
    private static EffectData spiderEffect(RandomSource random, DifficultyInstance difficulty, Spider entity) {
        List<Holder<MobEffect>> effects = new ArrayList<>(List.of(MobEffects.POISON, MobEffects.BLINDNESS, MobEffects.CONFUSION));
        List<Holder<MobEffect>> bufEffect = new ArrayList<>(List.of(MobEffects.WEAVING, MobEffects.INFESTED));
        Collections.shuffle(effects);
        Collections.shuffle(bufEffect);
        float f = difficulty.getSpecialMultiplier();
        EffectData effectData = giveEffectData(effects.get(0), random, f);
        if (random.nextFloat() < f * 0.15f) {
            entity.addEffect(new MobEffectInstance(bufEffect.get(0), effectData.duration() * 20, effectData.amplifier()));
        }
        return effectData;
    }

    private static EffectData EffectTo(RandomSource random, DifficultyInstance difficulty, Mob entity, List<Holder<MobEffect>> effectsOF, List<Holder<MobEffect>> bufEffectOF) {
        List<Holder<MobEffect>> effects = new ArrayList<>(effectsOF);
        List<Holder<MobEffect>> bufEffect = new ArrayList<>(bufEffectOF);
        Collections.shuffle(effects);
        Collections.shuffle(bufEffect);
        float f = difficulty.getSpecialMultiplier();
        EffectData effectData = giveEffectData(effects.get(0), random, f);
        if (bufEffectOF != null && !bufEffectOF.isEmpty() && random.nextFloat() < f * 0.05f) {
            entity.addEffect(new MobEffectInstance(bufEffect.get(0), effectData.duration() * 20, effectData.amplifier()));
        }
        return effectData;
    }

    private static EffectData giveEffectData(Holder<MobEffect> defaultEffect, RandomSource random, float specialMultiplier) {
        int duration = 0;
        float k = 1f;
        float c = 0f;
        while (k >= 0.01f) {
            if (duration > 60) break;
            if (random.nextFloat() < k * specialMultiplier) {
                duration += 4;
            }
            c += 0.01f;
            k -= Math.clamp(0.1f - c, 0.04f, 1);
        }
        int amplifier = 0;
        float l = 0.25f;
        while (l >= 0.01f) {
            if (amplifier > 3) break;
            if (random.nextFloat() < l * specialMultiplier) {
                amplifier++;
            }
            l -= random.nextInt(5) == 0 ? 0.05f : 0.1f;
        }
        return new EffectData(defaultEffect, duration * 20, amplifier);
    }

    public record EffectData(Holder<MobEffect> effect, int duration, int amplifier) {
    }
}
