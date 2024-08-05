package com.example.examplemod.goal;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.MyGuar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;

import java.util.EnumSet;

public class MyGuardianAttackGoal extends Goal {

    private final Guardian guardian;
    private int attackTime;
    private final boolean elder;

    public MyGuardianAttackGoal(Guardian p_32871_) {
        this.guardian = p_32871_;
        this.elder = p_32871_ instanceof ElderGuardian;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.guardian.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && (this.elder || this.guardian.getTarget() != null && this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0D);
    }

    public void start() {
        this.attackTime = -10;
        this.guardian.getNavigation().stop();
        LivingEntity livingentity = this.guardian.getTarget();
        if (livingentity != null) {
            this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
        }

        this.guardian.hasImpulse = true;
    }

    public void stop() {
        ((MyGuar) this.guardian).setActiveAttackTargetOver(0);
        this.guardian.setTarget((LivingEntity) null);
        ((MyGuar) this.guardian).getRandomStrollGoal().trigger();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.guardian.getTarget();
        if (livingentity != null) {
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            if (!this.guardian.hasLineOfSight(livingentity)) {
                this.guardian.setTarget((LivingEntity) null);
            } else {
                ++this.attackTime;
                if (this.attackTime == 0) {
                    ((MyGuar) this.guardian).setActiveAttackTargetOver(livingentity.getId());
                    if (!this.guardian.isSilent()) {
                        this.guardian.level().broadcastEntityEvent(this.guardian, (byte) 21);
                    }
                } else if (this.attackTime >= this.guardian.getAttackDuration()) {
                    float f = 1.0F;
                    DifficultyGeneral my_difficult = ((ILevel) this.guardian.level()).getDifficultyGen();
                    if (my_difficult == DifficultyGeneral.HARD) {
                        f += 2.0F;
                    }
                    if (my_difficult == DifficultyGeneral.INSANE) {
                        f += 3.0F;
                    }
                    if (my_difficult == DifficultyGeneral.NIGHTMARE) {
                        f += 4.0F;
                    }

                    if (this.elder) {
                        f += 2.0F;
                    }

                    livingentity.hurt(this.guardian.damageSources().indirectMagic(this.guardian, this.guardian), f);
                    livingentity.hurt(this.guardian.damageSources().mobAttack(this.guardian), (float) this.guardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.guardian.setTarget((LivingEntity) null);
                }

                super.tick();
            }
        }
    }
}
