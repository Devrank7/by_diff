package com.example.examplemod.intrtfaces;

import com.example.examplemod.difficulty.DifficultyGeneral;

public interface ILevel {
    DifficultyGeneral getDifficultyGen();
    DifficultyGeneral getDifficultyWithCheckClient();
}
