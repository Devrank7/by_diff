package com.example.examplemod.mixins.spawner;

import com.example.examplemod.MobCounts;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(LocalMobCapCalculator.class)
public class MixLocalMobCapCalculator {

    private final Map<ServerPlayer, MobCounts> playerMobCountsY = Maps.newHashMap();

    @Shadow
    private List<ServerPlayer> getPlayersNear(ChunkPos p_186508_) {
        return null;
    }

    /**
     * @author Devlink
     * @reason Fixes the mob cap
     */
    @Overwrite
    public void addMob(ChunkPos p_186513_, MobCategory p_186514_) {
        for (ServerPlayer serverplayer : this.getPlayersNear(p_186513_)) {
            this.playerMobCountsY.computeIfAbsent(serverplayer, p_186503_ -> new MobCounts()).add(p_186514_);
        }
    }

    /**
     * @author Devlink
     * @reason Fixes the mob cap
     */
    @Overwrite
    public boolean canSpawn(MobCategory p_186505_, ChunkPos p_186506_) {
        for (ServerPlayer serverplayer : this.getPlayersNear(p_186506_)) {
            MobCounts localmobcapcalculator$mobcounts = playerMobCountsY.get(serverplayer);
            if (localmobcapcalculator$mobcounts == null || localmobcapcalculator$mobcounts.canSpawn(p_186505_,(ServerLevel) serverplayer.level(),p_186506_.getMiddleBlockPosition(50))) {
                return true;
            }
        }

        return false;
    }
}
