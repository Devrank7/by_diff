package com.example.examplemod.mixins.boss;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(WitherBoss.class)
public abstract class MixWither extends Monster {

    public MixWither(EntityType<? extends WitherBoss> p_31437_, Level p_31438_) {
        super(p_31437_, p_31438_);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setHealth(this.getMaxHealth());
        this.xpReward = 50;
    }

    @Shadow
    public abstract int getInvulnerableTicks();

    @Shadow
    @Final
    private ServerBossEvent bossEvent;

    @Shadow
    public abstract void setInvulnerableTicks(int p_31511_);

    @Shadow
    @Final
    private int[] nextHeadUpdate;

    @Shadow
    @Final
    private int[] idleHeadUpdates;

    @Shadow
    public abstract int getAlternativeTarget(int p_31513_);

    @Shadow
    protected abstract void performRangedAttack(int p_31458_, LivingEntity p_31459_);

    @Shadow
    private void performRangedAttack(int p_31449_, double p_31450_, double p_31451_, double p_31452_, boolean p_31453_) {
    }

    @Shadow
    public abstract void setAlternativeTarget(int p_31455_, int p_31456_);

    @Shadow
    @Final
    private static TargetingConditions TARGETING_CONDITIONS;

    @Shadow
    private int destroyBlocksTick;

    @Shadow
    public abstract boolean isPowered();

    @Shadow
    public abstract boolean addEffect(MobEffectInstance p_182397_, @Nullable Entity p_182398_);


    /**
     * @author N/A
     * @reason Fix 1.19
     */
    @Overwrite
    protected void customServerAiStep() {
        DifficultyGeneral difficulty = ((ILevel) this.level()).getDifficultyGen();
        if (this.getInvulnerableTicks() > 0) {
            int k1 = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float) k1 / 220.0F);
            if (k1 <= 0) {
                this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), by_diff$getExplosionStrength(difficulty), false, Level.ExplosionInteraction.MOB);
                if (!this.isSilent()) {
                    this.level().globalLevelEvent(1023, this.blockPosition(), 0);
                }
            }

            this.setInvulnerableTicks(k1);
            if (this.tickCount % 10 == 0) {
                this.heal(by_diff$getHealAmount(difficulty));
            }
        } else {
            super.customServerAiStep();
            if (isPowered() && (difficulty == DifficultyGeneral.INSANE || DifficultyGeneral.NIGHTMARE == difficulty)) {
                int i = difficulty == DifficultyGeneral.NIGHTMARE ? 2 : 0;
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 2 + i), this);
            }

            for (int i = 1; i < 3; ++i) {
                if (this.tickCount >= this.nextHeadUpdate[i - 1]) {
                    this.nextHeadUpdate[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
                    if (difficulty.getId() >= 2) {
                        int i3 = i - 1;
                        int j3 = this.idleHeadUpdates[i - 1];
                        this.idleHeadUpdates[i3] = this.idleHeadUpdates[i - 1] + 1;
                        if (j3 > by_diff$getIdleHeadUpdateThreshold(difficulty)) {
                            float f = by_diff$getAttackRange(difficulty);
                            float f1 = by_diff$getAttackHeight(difficulty);
                            double d0 = Mth.nextDouble(this.random, this.getX() - f, this.getX() + f);
                            double d1 = Mth.nextDouble(this.random, this.getY() - f1, this.getY() + f1);
                            double d2 = Mth.nextDouble(this.random, this.getZ() - f, this.getZ() + f);
                            this.performRangedAttack(i + 1, d0, d1, d2, true);
                            this.idleHeadUpdates[i - 1] = 0;
                        }
                    }

                    int l1 = this.getAlternativeTarget(i);
                    if (l1 > 0) {
                        LivingEntity livingentity = (LivingEntity) this.level().getEntity(l1);
                        if (livingentity != null && this.canAttack(livingentity) && !(this.distanceToSqr(livingentity) > by_diff$getAttackDistanceThreshold(difficulty)) && this.hasLineOfSight(livingentity)) {
                            this.performRangedAttack(i + 1, livingentity);
                            this.nextHeadUpdate[i - 1] = this.tickCount + by_diff$getNextHeadUpdateTicks(difficulty);
                            this.idleHeadUpdates[i - 1] = 0;
                        } else {
                            this.setAlternativeTarget(i, 0);
                        }
                    } else {
                        List<LivingEntity> list = this.level().getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
                        if (!list.isEmpty()) {
                            LivingEntity livingentity1 = list.get(this.random.nextInt(list.size()));
                            this.setAlternativeTarget(i, livingentity1.getId());
                        }
                    }
                }
            }

            if (this.getTarget() != null) {
                this.setAlternativeTarget(0, this.getTarget().getId());
            } else {
                this.setAlternativeTarget(0, 0);
            }

            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                    int j1 = Mth.floor(this.getY());
                    int i2 = Mth.floor(this.getX());
                    int j2 = Mth.floor(this.getZ());
                    boolean flag = false;

                    for (int j = -1; j <= 1; ++j) {
                        for (int k2 = -1; k2 <= 1; ++k2) {
                            for (int k = 0; k <= 3; ++k) {
                                int l2 = i2 + j;
                                int l = j1 + k;
                                int i1 = j2 + k2;
                                BlockPos blockpos = new BlockPos(l2, l, i1);
                                BlockState blockstate = this.level().getBlockState(blockpos);
                                if (blockstate.canEntityDestroy(this.level(), blockpos, this) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                    flag = this.level().destroyBlock(blockpos, true, this) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.level().levelEvent((Player) null, 1022, this.blockPosition(), 0);
                    }
                }
            }

            if (this.tickCount % 20 == 0) {
                int m = 1;
                if ((difficulty == DifficultyGeneral.INSANE || difficulty == DifficultyGeneral.NIGHTMARE) && this.isPowered()) {
                    m = 1 == random.nextInt(difficulty == DifficultyGeneral.NIGHTMARE ? 3 : 5) ? 2 : 1;
                }
                this.heal(by_diff$getHealAmounts(difficulty) * m);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    /**
     * @author Devlink
     * @reason WIP
     */
    @Overwrite
    public boolean hurt(DamageSource p_31461_, float p_31462_) {
        if (this.isInvulnerableTo(p_31461_)) {
            return false;
        } else if (!p_31461_.is(DamageTypeTags.WITHER_IMMUNE_TO) && !(p_31461_.getEntity() instanceof WitherBoss)) {
            if (this.getInvulnerableTicks() > 0 && !p_31461_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                if (this.isPowered()) {
                    Entity entity = p_31461_.getDirectEntity();
                    if (entity instanceof AbstractArrow) {
                        return false;
                    }
                    if (!this.level().isClientSide) {
                        DifficultyGeneral difficulty = ((ILevel) this.level()).getDifficultyGen();
                        if (difficulty == DifficultyGeneral.INSANE || difficulty == DifficultyGeneral.NIGHTMARE) {
                            if (p_31462_ <= (difficulty == DifficultyGeneral.NIGHTMARE ? 8 : 4)) {
                                return false;
                            }
                        }
                        if (difficulty == DifficultyGeneral.INSANE || difficulty == DifficultyGeneral.NIGHTMARE) {
                            if (random.nextFloat() < (difficulty == DifficultyGeneral.NIGHTMARE ? 0.25F : 0.1F)) {
                                for (int i = 0; i < 3; ++i) {
                                    WitherSkeleton witherSkeleton = EntityType.WITHER_SKELETON.create(this.level());
                                    if (witherSkeleton != null) {
                                        witherSkeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                                        witherSkeleton.finalizeSpawn((ServerLevelAccessor) this.level(), this.level().getCurrentDifficultyAt(witherSkeleton.blockPosition()), MobSpawnType.STRUCTURE, null);
                                        this.level().addFreshEntity(witherSkeleton);
                                    }
                                }
                            }
                        }
                    }
                }

                Entity entity1 = p_31461_.getEntity();
                if (entity1 != null && entity1.getType().is(EntityTypeTags.WITHER_FRIENDS)) {
                    return false;
                } else {
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for (int i = 0; i < this.idleHeadUpdates.length; i++) {
                        this.idleHeadUpdates[i] = this.idleHeadUpdates[i] + 3;
                    }

                    return super.hurt(p_31461_, p_31462_);
                }
            }
        } else {
            return false;
        }
    }

    @Unique
    private float by_diff$getHealAmount(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 16.0F;
            case NIGHTMARE:
                yield 22.0F;
            default:
                yield 10.0F;
        };
    }

    @Unique
    private float by_diff$getHealAmounts(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield random.nextInt(3) + 1;
            case NIGHTMARE:
                yield random.nextInt(2, 5) + 1;
            default:
                yield 1.0F;
        };
    }

    @Unique
    private float by_diff$getExplosionStrength(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 13.0F;
            case NIGHTMARE:
                yield 17.0F;
            default:
                yield 7.0F;
        };
    }

    @Unique
    private int by_diff$getIdleHeadUpdateThreshold(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 14;
            case NIGHTMARE:
                yield 9;
            default:
                yield 20;
        };
    }

    @Unique
    private float by_diff$getAttackRange(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 16.0F;
            case NIGHTMARE:
                yield 22.0F;
            default:
                yield 10.0F;
        };
    }

    @Unique
    private float by_diff$getAttackHeight(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 10.0F;
            case NIGHTMARE:
                yield 14.0F;
            default:
                yield 5.0F;
        };
    }

    @Unique
    private double by_diff$getAttackDistanceThreshold(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE:
                yield 1225.0D;
            case NIGHTMARE:
                yield 2025.0D;
            default:
                yield 625.0D;
        };
    }

    @Unique
    private int by_diff$getNextHeadUpdateTicks(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case INSANE -> 22;
            case NIGHTMARE -> 15;
            default -> 40;
        };
    }
}
