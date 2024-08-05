package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.data.worldgen.biome.NetherBiomes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ghast.class)
public class MixGhast extends FlyingMob {

    public MixGhast(EntityType<? extends FlyingMob> p_20806_, Level p_20807_) {
        super(p_20806_, p_20807_);
    }

    @Shadow
    private int explosionPower;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getExplosionPower() {
        DifficultyGeneral difficultyGeneral = ((ILevel) (Object) level()).getDifficultyGen();
        int rand = this.getRandom().nextInt(100);
        int k = switch (difficultyGeneral) {
            case INSANE -> {
                if (rand == 0) {
                    yield 4;
                } else if (rand < 20) {
                    yield 3;
                } else if (rand < 80) {
                    yield 2;
                }
                yield 1;
            }
            case NIGHTMARE -> {
                if (rand == 0 && this.getRandom().nextInt(3) == 0) {
                    yield 6;
                } else if (rand < 4) {
                    yield 5;
                } else if (rand < 20) {
                    yield 4;
                } else if (rand < 90) {
                    yield 3;
                } else if (rand < 99) {
                    yield 2;
                }
                yield 1;
            }
            default -> 1;
        };
        return this.explosionPower * k;
    }
}
