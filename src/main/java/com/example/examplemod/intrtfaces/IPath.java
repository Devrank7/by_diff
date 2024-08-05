package com.example.examplemod.intrtfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Set;

public interface IPath {
    Path createPathZ(Set<BlockPos> p_26552_, int p_26553_, boolean p_26554_, int p_26555_);
}
