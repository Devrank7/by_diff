package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(ZombifiedPiglin.class)
public class MixZombifiedPiglin extends Zombie implements NeutralMob {
    public MixZombifiedPiglin(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    @Shadow
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    @Shadow
    public void setRemainingPersistentAngerTime(int p_21673_) {

    }

    @Nullable
    @Override
    @Shadow
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    @Shadow
    public void setPersistentAngerTarget(@Nullable UUID p_21672_) {

    }

    @Override
    @Shadow
    public void startPersistentAngerTimer() {

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34297_, DifficultyInstance p_34298_, MobSpawnType p_34299_, @Nullable SpawnGroupData p_34300_) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(p_34297_, p_34298_, p_34299_, p_34300_);
        DifficultyGeneral difficultyGeneral = ((IDifficultyInstance)p_34298_).getDifficultyGen();
        boolean flag = difficultyGeneral == DifficultyGeneral.INSANE || difficultyGeneral == DifficultyGeneral.NIGHTMARE;
        if (flag) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false) {
                @Override
                protected double getFollowDistance() {
                    return difficultyGeneral == DifficultyGeneral.NIGHTMARE ? random.nextInt(5) + 2 : random.nextInt(2) + 2;
                }
            });
        }
        return spawnGroupData;
    }
}
