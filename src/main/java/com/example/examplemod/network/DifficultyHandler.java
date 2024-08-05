package com.example.examplemod.network;

import com.example.examplemod.difficulty.DifficultyGeneral;

public class DifficultyHandler {

    private static DifficultyGeneral difficultyGeneral = null;

    public static boolean isNeedToUpdate = false;

    public static DifficultyGeneral getDifficultyGeneral() {
        return difficultyGeneral;
    }

    public static void setDifficultyGeneral(DifficultyGeneral difficultyGeneral) {
        DifficultyHandler.difficultyGeneral = difficultyGeneral;
    }
    public static boolean isNulls() {
        return difficultyGeneral == null;
    }
}
