package com.example.examplemod.intrtfaces;

import com.example.examplemod.difficulty.DifficultyGeneral;
import net.minecraft.world.level.LevelSettings;

public interface ILevelSettings {
    DifficultyGeneral getDifficultyGen();

    void setDifficultyGen(DifficultyGeneral difficultyGen);

    LevelSettings withDifficultGen(DifficultyGeneral difficultGen);
}
