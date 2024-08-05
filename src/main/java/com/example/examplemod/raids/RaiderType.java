package com.example.examplemod.raids;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

public enum RaiderType implements net.minecraftforge.common.IExtensibleEnum {
    VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5, 7, 8, 8, 9}),
    EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 4, 5}),
    PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2, 5, 6, 8, 10}),
    WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1, 2, 3, 2, 4}),
    RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2, 2, 3, 2, 4}),
    ILLUSIONER(EntityType.ILLUSIONER, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3});

    public static RaiderType[] VALUES = values();
    public final EntityType<? extends Raider> entityType;
    public final int[] spawnsPerWaveBeforeBonus;

    private RaiderType(final EntityType<? extends Raider> p_37821_, final int[] p_37822_) {
        this.entityType = p_37821_;
        this.spawnsPerWaveBeforeBonus = p_37822_;
    }

    /**
     * The waveCountsIn integer decides how many entities of the EntityType defined in typeIn will spawn in each wave.
     * For example, one ravager will always spawn in wave 3.
     */
    public static RaiderType create(String name, EntityType<? extends Raider> typeIn, int[] waveCountsIn) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    @Deprecated
    public void init() {
        VALUES = values();
    }
}
