package com.example.examplemod;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobCategory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class MobCounts {
    private final Object2IntMap<MobCategory> counts = new Object2IntOpenHashMap<>(MobCategory.values().length);
    Predicate<DifficultyGeneral> isNightmare = difficultyGeneral1 -> {
        return difficultyGeneral1 == DifficultyGeneral.NIGHTMARE;
    };

    public void add(MobCategory p_186518_) {
        this.counts.computeInt(p_186518_, (p_186520_, p_186521_) -> p_186521_ == null ? 1 : p_186521_ + 1);
    }

    public boolean canSpawn(MobCategory p_186523_, ServerLevel level, BlockPos blockPos) {
        DifficultyInstance difficultyInstance = level.getCurrentDifficultyAt(blockPos);
        DifficultyGeneral difficultyGeneral = ((IDifficultyInstance)difficultyInstance).getDifficultyGen();
        float moon = isNightmare.test(difficultyGeneral) ? Mth.clamp(level.getMoonBrightness(), 0.5f, 1.1f) : 0.5f;
        float f = Mth.clamp(difficultyInstance.getSpecialMultiplier() * moon, 1, 10);
        return this.counts.getOrDefault(p_186523_, 0) < p_186523_.getMaxInstancesPerChunk() * (MobCategory.MONSTER == p_186523_ ? f : 1.0F);
    }
}
