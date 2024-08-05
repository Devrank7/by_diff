package com.example.examplemod.intrtfaces;

import com.example.examplemod.difficulty.DifficultyGeneral;

public interface IWorldTab {
    void setDifficultyGen(DifficultyGeneral difficulty);

    DifficultyGeneral getDifficultyGen();
}
