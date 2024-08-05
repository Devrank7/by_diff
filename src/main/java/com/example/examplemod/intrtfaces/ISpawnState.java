package com.example.examplemod.intrtfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ISpawnState {
    boolean invokeCanSpawnForCategories(MobCategory category, ChunkPos chunkPos, ServerLevel level);

    boolean canSpawns(EntityType<?> p_47128_, BlockPos p_47129_, ChunkAccess p_47130_);

    void afterSpawns(Mob p_47132_, ChunkAccess p_47133_);
}
