package com.example.examplemod.mixins.mobs.goal;

import com.example.examplemod.goal.MyGuardianAttackGoal;
import com.example.examplemod.goal.MyGuardianAttackSelector;
import com.example.examplemod.intrtfaces.MyGuar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Function;

@Mixin(Guardian.class)
public abstract class MixGuardianAttackGoal extends Monster implements MyGuar {

    public MixGuardianAttackGoal(EntityType<? extends Monster> p_33002_, Level p_33003_, @Nullable RandomStrollGoal randomStrollGoal) {
        super(p_33002_, p_33003_);
        this.randomStrollGoal = randomStrollGoal;
    }

    @Shadow
    @Nullable
    protected RandomStrollGoal randomStrollGoal;

    @Shadow
    abstract void setActiveAttackTarget(int p_32818_);



    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void registerGoals() {
        MoveTowardsRestrictionGoal movetowardsrestrictiongoal = new MoveTowardsRestrictionGoal(this, 1.0D);
        this.randomStrollGoal = new RandomStrollGoal(this, 1.0D, 80);
        this.goalSelector.addGoal(4, new MyGuardianAttackGoal((Guardian) (Object) this));
        this.goalSelector.addGoal(5, movetowardsrestrictiongoal);
        this.goalSelector.addGoal(7, this.randomStrollGoal);
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Guardian.class, 12.0F, 0.01F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.randomStrollGoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        movetowardsrestrictiongoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, new MyGuardianAttackSelector((Guardian) (Object) this)));
    }

    @Override
    public void setActiveAttackTargetOver(int p_21672_) {
        setActiveAttackTarget(p_21672_);
    }

    @Override
    public RandomStrollGoal getRandomStrollGoal() {
        return randomStrollGoal;
    }
}
