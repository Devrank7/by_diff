package com.example.examplemod.difficulty;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;

public enum DifficultyGeneral {
    PEACEFUL(0, -1, "peaceful"),
    EASY(1, -1, "easy"),
    NORMAL(2, -1, "normal"),
    HARD(3, -1, "hard"),
    INSANE(4, 10, "insane"),
    NIGHTMARE(5, 50, "nightmare");
    private int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;

    DifficultyGeneral(int id, int difficulty, String name) {
        this.difficulty = difficulty;
        this.id = id;
        this.name = name;
    }

    public Component getName() {
        return Component.translatable("difficulty." + name);
    }
    public Component getInfo() {
        return Component.translatable("difficulty." + name + ".info");
    }

    public void setName(String name) {
        this.name = name;
    }

    public static DifficultyGeneral byId(int id) {
        return switch (id) {
            case 0 -> PEACEFUL;
            case 1 -> EASY;
            case 2 -> NORMAL;
            case 3 -> HARD;
            case 4 -> INSANE;
            case 5 -> NIGHTMARE;
            default -> null;
        };
    }

    public static Difficulty getSimpleDifficulty(DifficultyGeneral difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> Difficulty.PEACEFUL;
            case EASY -> Difficulty.EASY;
            case NORMAL -> Difficulty.NORMAL;
            case HARD, INSANE, NIGHTMARE -> Difficulty.HARD;
        };
    }
}
