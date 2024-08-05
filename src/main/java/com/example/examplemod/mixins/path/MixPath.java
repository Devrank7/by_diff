package com.example.examplemod.mixins.path;

import com.example.examplemod.intrtfaces.IPath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(PathNavigation.class)
public class MixPath implements IPath {
    @Shadow
    protected Path createPath(Set<BlockPos> p_26552_, int p_26553_, boolean p_26554_, int p_26555_) {
        return null;
    }
    @Override
    @Unique
    public Path createPathZ(Set<BlockPos> p_26552_, int p_26553_, boolean p_26554_, int p_26555_) {
        return createPath(p_26552_, p_26553_, p_26554_, p_26555_);
    }
}
