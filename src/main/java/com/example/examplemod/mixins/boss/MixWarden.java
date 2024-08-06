package com.example.examplemod.mixins.boss;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.IWarden;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.world.entity.monster.warden.Warden.applyDarknessAround;

@Mixin(Warden.class)
public abstract class MixWarden extends Monster implements IWarden {

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS
    )
            .setDarkenScreen(true);

    public MixWarden(EntityType<? extends Monster> p_33002_, Level p_33003_, AngerManagement angerManagement) {
        super(p_33002_, p_33003_);
        this.angerManagement = angerManagement;
    }

    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (p_341445_, p_341446_, p_341447_) -> {
        p_341446_.level().playSound(null, p_341446_, SoundEvents.BREEZE_DEFLECT, p_341446_.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(p_341445_, p_341446_, p_341447_);
    };

    @Shadow
    AngerManagement angerManagement;

    @Shadow
    protected abstract void syncClientAngerLevel();

    @Shadow
    public abstract boolean canTargetEntity(@Nullable Entity p_219386_);

    @Shadow
    public abstract Brain<Warden> getBrain();

    @Shadow
    public abstract AngerLevel getAngerLevel();

    @Shadow
    abstract boolean isDiggingOrEmerging();

    @Shadow
    public void increaseAngerAt(@javax.annotation.Nullable Entity p_219388_, int p_219389_, boolean p_219390_) {
    }

    @Shadow
    public void setAttackTarget(LivingEntity p_219460_) {
    }

    /**
     * @author
     * @reason
     */

    @Override
    public void aiStep() {
        super.aiStep();
        boolean flag = this.isPowered();
        for (int l = 0; l < 3; l++) {
            float f = 0.3F * this.getScale();
            double d8 = this.getHeadX(l);
            double d10 = this.getHeadY(l);
            double d2 = this.getHeadZ(l);
            this.level()
                    .addParticle(
                            ParticleTypes.SMOKE,
                            d8 + this.random.nextGaussian() * (double) f,
                            d10 + this.random.nextGaussian() * (double) f,
                            d2 + this.random.nextGaussian() * (double) f,
                            0.0,
                            0.0,
                            0.0
                    );
            if (flag && this.level().random.nextInt(4) == 0) {
                this.level()
                        .addParticle(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.7F, 0.7F, 0.5F),
                                d8 + this.random.nextGaussian() * (double) f,
                                d10 + this.random.nextGaussian() * (double) f,
                                d2 + this.random.nextGaussian() * (double) f,
                                0.0,
                                0.0,
                                0.0
                        );
            }
        }
        if (getAngerLevel().isAngry() && this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
            boolean flag1 = false;
            boolean flag2 = false;
            AABB aabb = this.getBoundingBox().inflate(0.2);
            for (BlockPos blockpos : BlockPos.betweenClosed(
                    Mth.floor(aabb.minX),
                    Mth.floor(aabb.maxY - 1),
                    Mth.floor(aabb.minZ),
                    Mth.floor(aabb.maxX),
                    Mth.floor(aabb.maxY),
                    Mth.floor(aabb.maxZ))) {
                BlockState blockstate = this.level().getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (block.defaultDestroyTime() < 30 && block.defaultDestroyTime() != 0) {
                    flag2 = true;
                    break;
                }
            }
            if (flag2) {
                for (BlockPos blockpos : BlockPos.betweenClosed(
                        Mth.floor(aabb.minX),
                        Mth.floor(aabb.minY + 0.2f),
                        Mth.floor(aabb.minZ),
                        Mth.floor(aabb.maxX),
                        Mth.floor(aabb.maxY),
                        Mth.floor(aabb.maxZ)
                )) {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (block.defaultDestroyTime() < 30) {
                        flag1 = this.level().destroyBlock(blockpos, true, this) || flag1;
                    }
                }

                if (!flag1 && this.onGround()) {
                    this.jumpFromGround();
                }
            }
        }

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void customServerAiStep() {
        ServerLevel serverlevel = (ServerLevel) this.level();
        serverlevel.getProfiler().push("wardenBrain");
        DifficultyGeneral my_difficulty = ((ILevel) serverlevel).getDifficultyGen();

        this.getBrain().tick(serverlevel, (Warden) (Object) this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 120 == 0) {
            int radius = switch (my_difficulty) {
                case INSANE -> 30;
                case NIGHTMARE -> 40;
                default -> 20;
            };
            applyDarknessAround(serverlevel, this.position(), this, radius);
        }

        if (this.tickCount % 20 == 0) {
            if (isPowered() && getAngerLevel().isAngry() && (my_difficulty == DifficultyGeneral.INSANE || my_difficulty == DifficultyGeneral.NIGHTMARE)) {
                int i = my_difficulty == DifficultyGeneral.NIGHTMARE ? 3 : 1;
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50, i));
            }
            this.angerManagement.tick(serverlevel, this::canTargetEntity);
            this.syncClientAngerLevel();
        }

        WardenAi.updateActivity((Warden) (Object) this);
        this.bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean doHurtTarget(Entity target) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        DifficultyGeneral my_difficulty = ((ILevel) target.level()).getDifficultyGen();
        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
        int cooldown = switch (my_difficulty) {
            case INSANE -> 30;
            case NIGHTMARE -> 18;
            default -> 40;
        };
        SonicBoom.setCooldown(this, cooldown);
        return super.doHurtTarget(target);
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public boolean hurt(DamageSource p_219381_, float p_219382_) {
        DifficultyGeneral difficultyGeneral = ((ILevel) level()).getDifficultyGen();
        float damage = p_219382_;
        if (isPowered() && (difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE)) {
            if (p_219381_.getDirectEntity() instanceof Projectile projectile) {
                damage = (p_219382_ / 5);
                ProjectileDeflection deflection = this.deflection(projectile);
                if (deflection != ProjectileDeflection.NONE) {
                    projectile.deflect(deflection, this, projectile.getOwner(), false);
                }
                float force = difficultyGeneral == DifficultyGeneral.NIGHTMARE ? 4.5F : 2.2f;
                if (damage < force) return false;
            }
        }
        boolean flag = super.hurt(p_219381_, damage);
        if (!this.level().isClientSide && !this.isNoAi() && !this.isDiggingOrEmerging()) {
            Entity entity = p_219381_.getEntity();
            this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
                    && entity instanceof LivingEntity livingentity
                    && (p_219381_.isDirect() || this.closerThan(livingentity, 5.0))) {
                this.setAttackTarget(livingentity);
            }
        }

        return flag;
    }

    @Override
    public ProjectileDeflection deflection(Projectile p_335920_) {
        return PROJECTILE_DEFLECTION;

    }

    @Override
    @Unique
    public boolean isPowered() {
        DifficultyGeneral difficultyGeneral = ((ILevel) level()).getDifficultyWithCheckClient();
        boolean powered = difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
        return powered && this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    private double getHeadX(int p_31515_) {
        if (p_31515_ <= 0) {
            return this.getX();
        } else {
            float f = (this.yBodyRot + (float) (180 * (p_31515_ - 1))) * (float) (Math.PI / 180.0);
            float f1 = Mth.cos(f);
            return this.getX() + (double) f1 * 1.3 * (double) this.getScale();
        }
    }

    private double getHeadY(int p_31517_) {
        float f = p_31517_ <= 0 ? 3.0F : 2.2F;
        return this.getY() + (double) (f * this.getScale());
    }

    private double getHeadZ(int p_31519_) {
        if (p_31519_ <= 0) {
            return this.getZ();
        } else {
            float f = (this.yBodyRot + (float) (180 * (p_31519_ - 1))) * (float) (Math.PI / 180.0);
            float f1 = Mth.sin(f);
            return this.getZ() + (double) f1 * 1.3 * (double) this.getScale();
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer p_31483_) {
        super.startSeenByPlayer(p_31483_);
        this.bossEvent.addPlayer(p_31483_);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
        this.bossEvent.removePlayer(p_31488_);
    }
}
