package com.example.examplemod.intrtfaces;

import com.example.examplemod.difficulty.DifficultyGeneral;
import net.minecraft.world.level.Level;

public interface IDifficultyInstance {
    void preCalculateDifficulty(Level level);
    DifficultyGeneral getDifficultyGen();
}
