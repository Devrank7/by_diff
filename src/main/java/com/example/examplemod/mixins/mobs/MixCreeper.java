package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Creeper.class)
public class MixCreeper extends Monster {
    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    public MixCreeper(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_);
        DifficultyGeneral difficultyGeneral = ((IDifficultyInstance) p_21435_).getDifficultyGen();
        float f = switch (difficultyGeneral) {
            case INSANE -> 0.2F * (p_21435_.getSpecialMultiplier() * 0.3f);
            case NIGHTMARE -> 0.5f * (p_21435_.getSpecialMultiplier() * 0.3f);
            default -> 0.0F;
        };
        if (random.nextFloat() < f) {
            entityData.set(DATA_IS_POWERED, true);
        }
        return spawnGroupData;
    }
}
