package com.example.examplemod.event.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.difficulty.effect.RandomEffectUtils;
import com.example.examplemod.goal.ShootAtGoal;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.network.DifficultyHandler;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ModEvent {

    static RandomSource random = RandomSource.createThreadSafe();
    public static final List<Holder<MobEffect>> ALL_EFFECTS = List.of(
            MobEffects.MOVEMENT_SPEED,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SPEED,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.DAMAGE_BOOST,
            MobEffects.HEAL,
            MobEffects.HARM,
            MobEffects.JUMP,
            MobEffects.CONFUSION,
            MobEffects.REGENERATION,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.BLINDNESS,
            MobEffects.NIGHT_VISION,
            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            MobEffects.GLOWING,
            MobEffects.LEVITATION,
            MobEffects.LUCK,
            MobEffects.UNLUCK,
            MobEffects.SLOW_FALLING,
            MobEffects.CONDUIT_POWER,
            MobEffects.DOLPHINS_GRACE,
            MobEffects.BAD_OMEN,
            MobEffects.HERO_OF_THE_VILLAGE,
            MobEffects.DARKNESS,
            MobEffects.TRIAL_OMEN,
            MobEffects.RAID_OMEN,
            MobEffects.WIND_CHARGED,
            MobEffects.WEAVING,
            MobEffects.OOZING,
            MobEffects.INFESTED
    );
    public static List<Holder<MobEffect>> HURT_EFFECTS = ALL_EFFECTS.stream().filter(mobEffectHolder -> {
        boolean flag = !mobEffectHolder.get().isInstantenous();
        boolean flag1 = !(mobEffectHolder == MobEffects.LEVITATION);
        boolean flag2 = !(mobEffectHolder == MobEffects.WITHER);
        boolean flag3 = !(mobEffectHolder == MobEffects.BAD_OMEN);
        boolean flag4 = !(mobEffectHolder == MobEffects.RAID_OMEN);
        boolean flag5 = !(mobEffectHolder == MobEffects.TRIAL_OMEN);
        return mobEffectHolder.get().getCategory() == MobEffectCategory.HARMFUL && flag && flag1 && flag2 && flag3 && flag4 && flag5;
    }).toList();

    @SubscribeEvent
    public static void arrowTop(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof AbstractArrow) {
            AbstractArrow arrow = (AbstractArrow) event.getEntity();
            if (event.getRayTraceResult() instanceof BlockHitResult blockHitResult) {
                BlockPos hitPos = blockHitResult.getBlockPos();
                BlockState hitBlockState = arrow.level().getBlockState(hitPos);
                if (arrow.getOwner() instanceof AbstractSkeleton skeleton) {
                    for (Goal goal : skeleton.goalSelector.getAvailableGoals()) {
                        if (goal instanceof WrappedGoal g) {
                            if (g.getGoal() instanceof ShootAtGoal shootGoal) {
                                if (shootGoal.getBlockClass().isInstance(hitBlockState.getBlock())) {
                                    Block castBlock = shootGoal.getBlockClass().cast(hitBlockState.getBlock());
                                    shootGoal.incrementRightShots();
                                    float destroyTime = castBlock.defaultDestroyTime();
                                    int bounds = (int) (destroyTime * 50);
                                    int origin = (int) (destroyTime * 16);
                                    if (shootGoal.getRightShoots() * (shootGoal.getStrengthSkileton() / 10) > skeleton.getRandom().nextInt(origin, bounds)) {
                                        event.getEntity().level().destroyBlock(hitPos, false, skeleton);
                                        event.getProjectile().remove(Entity.RemovalReason.DISCARDED);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !event.getEntity().level().isClientSide) {
            Entity entity = event.getSource().getDirectEntity();
            if (entity == null) return;
            DifficultyInstance difficultyInstance = entity.level().getCurrentDifficultyAt(entity.blockPosition());
            DifficultyGeneral difficulty = ((IDifficultyInstance) difficultyInstance).getDifficultyGen();
            if (entity instanceof Monster monster) {
                if (difficulty.getId() <= 3) return;
                giveEffect(difficultyInstance, player, monster);
            } else if (entity instanceof Projectile projectile) {
                if (projectile.getOwner() instanceof Monster monster) {
                    if (difficulty.getId() <= 3) return;
                    giveEffect(difficultyInstance, player, monster);
                }
            }
        }
    }

    private static void giveEffect(DifficultyInstance difficultyInstance, Player player, Monster monster) {
        float f = (float) Math.pow(difficultyInstance.getSpecialMultiplier(), 2) * 0.015f;
        if (monster.getRandom().nextFloat() < f) {
            RandomEffectUtils.EffectData effectData = RandomEffectUtils.getRandomEffectForType(monster, difficultyInstance, monster.getRandom());
            if (effectData == null) return;
            player.addEffect(new MobEffectInstance(effectData.effect(), effectData.duration(), effectData.amplifier()));
        }
    }

    @SubscribeEvent
    public static void join(ServerStartedEvent event) {
        DifficultyHandler.isNeedToUpdate = true;
        System.err.println("Server started");
    }
}
