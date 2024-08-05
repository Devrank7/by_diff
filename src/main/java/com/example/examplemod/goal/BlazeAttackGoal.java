package com.example.examplemod.goal;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IBlaze;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BlazeAttackGoal extends Goal{

    private final Blaze blaze;
    private int attackStep;
    private int attackTime;
    private int lastSeen;

    public BlazeAttackGoal(Blaze p_32247_) {
        this.blaze = p_32247_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.blaze.getTarget();
        return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
    }

    @Override
    public void start() {
        this.attackStep = 0;
    }

    @Override
    public void stop() {
        ((IBlaze)this.blaze).setChargeds(false);
        this.lastSeen = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.attackTime--;
        LivingEntity livingentity = this.blaze.getTarget();
        if (livingentity != null) {
            DifficultyGeneral difficultyGeneral = ((ILevel)this.blaze.level()).getDifficultyGen();
            int k = switch (difficultyGeneral) {
                case INSANE -> blaze.getRandom().nextBoolean() ? 2 : 1;
                case NIGHTMARE -> blaze.getRandom().nextBoolean() ? 3 : 7;
                default -> 0;
            };
            boolean flag = this.blaze.getSensing().hasLineOfSight(livingentity);
            if (flag) {
                this.lastSeen = 0;
            } else {
                this.lastSeen++;
            }

            double d0 = this.blaze.distanceToSqr(livingentity);
            if (d0 < 4.0) {
                if (!flag) {
                    return;
                }

                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.blaze.doHurtTarget(livingentity);
                }

                this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                double d1 = livingentity.getX() - this.blaze.getX();
                double d2 = livingentity.getY(0.5) - this.blaze.getY(0.5);
                double d3 = livingentity.getZ() - this.blaze.getZ();
                if (this.attackTime <= 0) {
                    this.attackStep++;
                    if (this.attackStep == 1) {
                        this.attackTime = 60;
                        ((IBlaze)this.blaze).setChargeds(true);
                    } else if (this.attackStep <= 4 + k) {
                        this.attackTime = 6;
                    } else {
                        this.attackTime = 100;
                        this.attackStep = 0;
                        ((IBlaze)this.blaze).setChargeds(false);
                    }

                    if (this.attackStep > 1 && this.attackStep <= 4 + k) {
                        double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5;
                        if (!this.blaze.isSilent()) {
                            this.blaze.level().levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                        }

                        for (int i = 0; i < 1; i++) {
                            Vec3 vec3 = new Vec3(
                                    this.blaze.getRandom().triangle(d1, 2.297 * d4), d2, this.blaze.getRandom().triangle(d3, 2.297 * d4)
                            );
                            SmallFireball smallfireball = new SmallFireball(this.blaze.level(), this.blaze, vec3.normalize());
                            smallfireball.setPos(smallfireball.getX(), this.blaze.getY(0.5) + 0.5, smallfireball.getZ());
                            this.blaze.level().addFreshEntity(smallfireball);
                        }
                    }
                }

                this.blaze.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
                this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
            }

            super.tick();
        }
    }

    private double getFollowDistance() {
        return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
