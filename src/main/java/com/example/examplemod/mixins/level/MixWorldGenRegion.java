package com.example.examplemod.mixins.level;

import com.example.examplemod.intrtfaces.IDifficultyInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldGenRegion.class)
public class MixWorldGenRegion {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_9585_) {
        WorldGenRegion self = (WorldGenRegion) (Object) this;
        if (!self.hasChunk(SectionPos.blockToSectionCoord(p_9585_.getX()), SectionPos.blockToSectionCoord(p_9585_.getZ()))) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            DifficultyInstance difficultyInstance = new DifficultyInstance(self.getLevel().getDifficulty(), self.getLevel().getDayTime(), 0L, self.getLevel().getMoonBrightness());
            ((IDifficultyInstance) (Object) difficultyInstance).preCalculateDifficulty(self.getLevel());
            return difficultyInstance;
        }
    }
}
