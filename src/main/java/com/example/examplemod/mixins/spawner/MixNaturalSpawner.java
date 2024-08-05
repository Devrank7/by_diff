package com.example.examplemod.mixins.spawner;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.ISpawnState;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(NaturalSpawner.class)
public class MixNaturalSpawner {
    private static final MobCategory[] SPAWN_CATEGORIES = Stream.of(MobCategory.values()).filter(p_47037_ -> p_47037_ != MobCategory.MISC).toArray(MobCategory[]::new);
    private static Predicate<DifficultyGeneral> isNightmare = difficultyGeneral1 -> difficultyGeneral1 == DifficultyGeneral.NIGHTMARE;
    @Shadow
    public static void spawnCategoryForChunk(
            MobCategory p_47046_, ServerLevel p_47047_, LevelChunk p_47048_, NaturalSpawner.SpawnPredicate p_47049_, NaturalSpawner.AfterSpawnCallback p_47050_
    ) {}
    /**
     * @author Devlink
     * @reason Fix
     */
    @Overwrite
    public static void spawnForChunk(
            ServerLevel p_47030_, LevelChunk p_47031_, NaturalSpawner.SpawnState p_47032_, boolean p_47033_, boolean p_47034_, boolean p_47035_
    ) {
        p_47030_.getProfiler().push("spawner");
        ISpawnState spawnState = ((ISpawnState) p_47032_);

        for (MobCategory mobcategory : SPAWN_CATEGORIES) {
            if ((p_47033_ || !mobcategory.isFriendly())
                    && (p_47034_ || mobcategory.isFriendly())
                    && (p_47035_ || !mobcategory.isPersistent())
                    && spawnState.invokeCanSpawnForCategories(mobcategory, p_47031_.getPos(), p_47030_)) {
                spawnCategoryForChunk(mobcategory, p_47030_, p_47031_, spawnState::canSpawns, spawnState::afterSpawns);
            }
        }

        p_47030_.getProfiler().pop();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel p_47025_, ChunkAccess p_47026_, BlockPos.MutableBlockPos p_47027_, double p_47028_) {
        if (p_47028_ <= 576.0) {
            return false;
        } else {
            DifficultyGeneral difficultyGeneral = ((ILevel)p_47025_).getDifficultyGen();
            float i = (float) Math.pow(p_47025_.getMoonBrightness() * 10, 2.0F) / 7;
            float moon_mimus = isNightmare.test(difficultyGeneral) ? i : 0;
            return !p_47025_.getSharedSpawnPos()
                    .closerToCenterThan(new Vec3((double) p_47027_.getX() + 0.5, (double) p_47027_.getY(), (double) p_47027_.getZ() + 0.5), 24.0 - Mth.ceil(moon_mimus)) && (Objects.equals(new ChunkPos(p_47027_), p_47026_.getPos()) || p_47025_.isNaturalSpawningAllowed(p_47027_));
        }
    }

    @Mixin(NaturalSpawner.SpawnState.class)
    public static abstract class MySpawnStates implements ISpawnState {
        @Shadow
        @Final
        private LocalMobCapCalculator localMobCapCalculator;
        private static Predicate<DifficultyGeneral> isNightmares = difficultyGeneral1 -> difficultyGeneral1 == DifficultyGeneral.NIGHTMARE;

        @Unique
        boolean canSpawnForCategory(MobCategory category, ChunkPos chunkPos, ServerLevel level) {
            DifficultyInstance difficultyInstance = level.getCurrentDifficultyAt(chunkPos.getMiddleBlockPosition(50));
            DifficultyGeneral difficultyGeneral = ((IDifficultyInstance) difficultyInstance).getDifficultyGen();
            float moon = isNightmares.test(difficultyGeneral) ? Mth.clamp(level.getMoonBrightness(), 0.5f, 1.1f) : 0.5f;
            float f = Mth.clamp(difficultyInstance.getSpecialMultiplier() * moon, 1, 10);
            int i = (int) (category.getMaxInstancesPerChunk() * (category == MobCategory.MONSTER ? f : 1.0F)) * spawnableChunkCount / (int) Math.pow(17.0D, 2.0D);
            return this.mobCategoryCounts.getInt(category) < i && this.localMobCapCalculator.canSpawn(category, chunkPos);
        }

        @Shadow
        private boolean canSpawn(EntityType<?> p_47128_, BlockPos p_47129_, ChunkAccess p_47130_) {
            return true;
        }

        @Shadow
        private void afterSpawn(Mob p_47132_, ChunkAccess p_47133_) {
        }

        @Shadow
        @Final
        private int spawnableChunkCount;

        @Shadow
        @Final
        private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;

        @Override
        @Unique
        public boolean invokeCanSpawnForCategories(MobCategory category, ChunkPos chunkPos, ServerLevel level) {
            return canSpawnForCategory(category, chunkPos, level);
        }

        @Override
        @Unique
        public boolean canSpawns(EntityType<?> p_47128_, BlockPos p_47129_, ChunkAccess p_47130_) {
            return canSpawn(p_47128_, p_47129_, p_47130_);
        }

        @Override
        @Unique
        public void afterSpawns(Mob p_47132_, ChunkAccess p_47133_) {
            afterSpawn(p_47132_, p_47133_);
        }
    }
}
