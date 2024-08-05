package com.example.examplemod.goal;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.IShulker;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.Optional;

public class ShulkerGoal {

    public static class ShulkerAttackGoal extends Goal {
        private int attackTime;
        private int shulkerShoot;
        private Shulker shulker;

        private DifficultyGeneral difficultyGeneral = DifficultyGeneral.NORMAL;

        public ShulkerAttackGoal(Shulker shulker) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.shulker = shulker;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = shulker.getTarget();
            return livingentity != null && livingentity.isAlive() ? shulker.level().getDifficulty() != Difficulty.PEACEFUL : false;
        }

        @Override
        public void start() {
            difficultyGeneral = ((ILevel)shulker.level()).getDifficultyGen();
            this.attackTime = 20;
            shulkerShoot = 0;
            ((IShulker)shulker).setRawPeekAmountY(100);
        }

        @Override
        public void stop() {
            ((IShulker)shulker).setRawPeekAmountY(0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (shulker.level().getDifficulty() != Difficulty.PEACEFUL) {
                this.attackTime--;
                LivingEntity livingentity = shulker.getTarget();
                if (livingentity != null) {
                    shulker.getLookControl().setLookAt(livingentity, 180.0F, 180.0F);
                    double d0 = shulker.distanceToSqr(livingentity);
                    if (d0 < 400.0) {
                        if (this.attackTime <= 0) {
                            boolean mayThrowSeveralBullets = difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
                            if (mayThrowSeveralBullets) {
                                int k = switch (difficultyGeneral) {
                                    case INSANE -> shulker.getRandom().nextBoolean() ? 1 : 2;
                                    case NIGHTMARE -> shulker.getRandom().nextBoolean() ? 2 : shulker.getRandom().nextBoolean() ? 3 : 1;
                                    default -> 1;
                                };
                                if (shulkerShoot > k) {
                                    this.attackTime = 20 + shulker.getRandom().nextInt(10) * 20 / 2;
                                    shulkerShoot = 0;
                                } else {
                                    this.attackTime = 5;
                                    shulkerShoot++;
                                }
                            } else {
                                this.attackTime = 20 + shulker.getRandom().nextInt(10) * 20 / 2;
                                shulkerShoot = 0;
                            }
                            shulker.level()
                                    .addFreshEntity(new ShulkerBullet(shulker.level(), shulker, livingentity, shulker.getAttachFace().getAxis()));
                            shulker.playSound(
                                    SoundEvents.SHULKER_SHOOT, 2.0F, (shulker.getRandom().nextFloat() - shulker.getRandom().nextFloat()) * 0.2F + 1.0F
                            );
                        }
                    } else {
                        shulker.setTarget(null);
                    }

                    super.tick();
                }
            }
        }
    }

    public static class ShulkerBodyRotationControl extends BodyRotationControl {
        public ShulkerBodyRotationControl(Mob p_149816_) {
            super(p_149816_);
        }

        @Override
        public void clientTick() {
        }
    }

    public static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
        public ShulkerDefenseAttackGoal(Shulker p_33496_) {
            super(p_33496_, LivingEntity.class, 10, true, false, p_33501_ -> p_33501_ instanceof Enemy);
        }

        @Override
        public boolean canUse() {
            return this.mob.getTeam() == null ? false : super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double p_33499_) {
            Direction direction = ((Shulker)this.mob).getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, p_33499_, p_33499_);
            } else {
                return direction.getAxis() == Direction.Axis.Z
                        ? this.mob.getBoundingBox().inflate(p_33499_, p_33499_, 4.0)
                        : this.mob.getBoundingBox().inflate(p_33499_, 4.0, p_33499_);
            }
        }
    }

    public class ShulkerLookControl extends LookControl {

        private Shulker shulker;
        public ShulkerLookControl(final Mob p_149820_) {
            super(p_149820_);
            this.shulker = (Shulker)p_149820_;
        }

        @Override
        protected void clampHeadRotationToBody() {
        }

        @Override
        protected Optional<Float> getYRotD() {
            Direction direction = shulker.getAttachFace().getOpposite();
            Vector3f vector3f = direction.getRotation().transform(new Vector3f(Util.make(() -> {
                Vec3i vec3i = Direction.SOUTH.getNormal();
                return new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            })));
            Vec3i vec3i = direction.getNormal();
            Vector3f vector3f1 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            vector3f1.cross(vector3f);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getEyeY();
            double d2 = this.wantedZ - this.mob.getZ();
            Vector3f vector3f2 = new Vector3f((float)d0, (float)d1, (float)d2);
            float f = vector3f1.dot(vector3f2);
            float f1 = vector3f.dot(vector3f2);
            return !(Math.abs(f) > 1.0E-5F) && !(Math.abs(f1) > 1.0E-5F)
                    ? Optional.empty()
                    : Optional.of((float)(Mth.atan2((double)(-f), (double)f1) * 180.0F / (float)Math.PI));
        }

        @Override
        protected Optional<Float> getXRotD() {
            return Optional.of(0.0F);
        }
    }

    public static class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal<Player> {
        private final Shulker shulker;
        public ShulkerNearestAttackGoal(final Shulker p_33505_) {
            super(p_33505_, Player.class, true);
            shulker = p_33505_;
        }

        @Override
        public boolean canUse() {
            return shulker.level().getDifficulty() == Difficulty.PEACEFUL ? false : super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double p_33508_) {
            Direction direction = ((Shulker)this.mob).getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, p_33508_, p_33508_);
            } else {
                return direction.getAxis() == Direction.Axis.Z
                        ? this.mob.getBoundingBox().inflate(p_33508_, p_33508_, 4.0)
                        : this.mob.getBoundingBox().inflate(p_33508_, 4.0, p_33508_);
            }
        }
    }

    public static class ShulkerPeekGoal extends Goal {
        private int peekTime;

        private final Shulker shulker;
        public ShulkerPeekGoal(final Shulker p_33513_) {
            this.shulker = p_33513_;
        }

        @Override
        public boolean canUse() {
            return shulker.getTarget() == null
                    && shulker.getRandom().nextInt(reducedTickDelay(40)) == 0
                    && ((IShulker)shulker).canStayAtY(shulker.blockPosition(), shulker.getAttachFace());
        }

        @Override
        public boolean canContinueToUse() {
            return shulker.getTarget() == null && this.peekTime > 0;
        }

        @Override
        public void start() {
            this.peekTime = this.adjustedTickDelay(20 * (1 + shulker.getRandom().nextInt(3)));
            ((IShulker)shulker).setRawPeekAmountY(30);
        }

        @Override
        public void stop() {
            if (shulker.getTarget() == null) {
                ((IShulker)shulker).setRawPeekAmountY(0);
            }
        }

        @Override
        public void tick() {
            this.peekTime--;
        }
    }
}
