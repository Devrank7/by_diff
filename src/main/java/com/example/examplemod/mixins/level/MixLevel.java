package com.example.examplemod.mixins.level;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.network.DifficultyHandler;
import com.example.examplemod.network.ModMessage;
import com.example.examplemod.network.PGetServerDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public class MixLevel implements ILevel {
    @Override
    @Unique
    public DifficultyGeneral getDifficultyGen() {
        Level level = (Level) (Object) this;
        if (level.getServer() != null) {
            return ((ILevelSettings) (Object) level.getServer().getWorldData().getLevelSettings()).getDifficultyGen();
        }
        return DifficultyGeneral.NORMAL;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46730_) {
        Level self = (Level) (Object) this;
        long i = 0L;
        float f = 0.0F;
        if (self.hasChunkAt(p_46730_)) {
            f = self.getMoonBrightness();
            i = self.getChunkAt(p_46730_).getInhabitedTime();
        }
        DifficultyInstance difficultyInstance = new DifficultyInstance(self.getDifficulty(), self.getDayTime(), i, f);
        ((IDifficultyInstance) (Object) difficultyInstance).preCalculateDifficulty(self);
        return difficultyInstance;
    }

    @Override
    public DifficultyGeneral getDifficultyWithCheckClient() {
        Level level = (Level) (Object) this;
        if (level.getServer() != null) {
            return ((ILevelSettings) (Object) level.getServer().getWorldData().getLevelSettings()).getDifficultyGen();
        }
        if (DifficultyHandler.isNeedToUpdate) {
            ModMessage.sendToServer(new PGetServerDifficulty());
            while (DifficultyHandler.isNulls()) {
                Thread.onSpinWait();
                System.err.println("IS NULLS");
            }
            DifficultyGeneral difficultyGeneral = DifficultyHandler.getDifficultyGeneral();
            System.err.println("DIFFICULTY: " + difficultyGeneral);
            System.err.println("RESET DIFFICULTY: " + difficultyGeneral);
            DifficultyHandler.isNeedToUpdate = false;
            return difficultyGeneral;
        }
        if (level.getRandom().nextInt(100) == 0) {
            System.err.println("isNulls: " + DifficultyHandler.isNulls());
        }
        return DifficultyHandler.isNulls() ? DifficultyGeneral.NORMAL : DifficultyHandler.getDifficultyGeneral();
    }
}
