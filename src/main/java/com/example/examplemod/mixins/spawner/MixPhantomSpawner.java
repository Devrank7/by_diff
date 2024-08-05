package com.example.examplemod.mixins.spawner;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PhantomSpawner.class)
public class MixPhantomSpawner implements CustomSpawner {
    @Shadow
    private int nextTick;

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public int tick(ServerLevel p_64576_, boolean p_64577_, boolean p_64578_) {
        if (!p_64577_) {
            return 0;
        } else if (!p_64576_.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
            return 0;
        } else {
            RandomSource randomsource = p_64576_.random;
            this.nextTick--;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick = this.nextTick + (60 + randomsource.nextInt(60)) * 20;
                DifficultyGeneral dg = ((ILevel) p_64576_).getDifficultyGen();
                int r = switch (dg) {
                    case INSANE -> 3;
                    case NIGHTMARE -> 0;
                    default -> 5;
                };
                if (p_64576_.getSkyDarken() < r && p_64576_.dimensionType().hasSkyLight()) {
                    return 0;
                } else {
                    int i = 0;

                    for (ServerPlayer serverplayer : p_64576_.players()) {
                        if (!serverplayer.isSpectator()) {
                            BlockPos blockpos = serverplayer.blockPosition();
                            DifficultyInstance difficultyinstance = p_64576_.getCurrentDifficultyAt(blockpos);
                            var vanillaPosition = (!p_64576_.dimensionType().hasSkyLight() || blockpos.getY() >= p_64576_.getSeaLevel() && p_64576_.canSeeSky(blockpos));
                            DifficultyGeneral difficultyGeneral = ((ILevel) p_64576_).getDifficultyGen();
                            int h = switch (difficultyGeneral) {
                                case INSANE -> difficultyGeneral.getId() + (randomsource.nextBoolean() ? 2 : randomsource.nextBoolean() ? 3 : 1);
                                case NIGHTMARE -> difficultyGeneral.getId() + (randomsource.nextBoolean() ? 5 : 6);
                                default -> difficultyGeneral.getId();
                            };
                            var count = 1 + randomsource.nextInt(h + 1);
                            var event = net.minecraftforge.event.ForgeEventFactory.onPlayerSpawnPhantom(serverplayer, count);
                            var eventResult = event.getResult();
                            if (eventResult.isDenied()) continue;
                            if (vanillaPosition || eventResult.isAllowed()) {
                                if (difficultyinstance.isHarderThan(randomsource.nextFloat() * 3.0F)) {
                                    ServerStatsCounter serverstatscounter = serverplayer.getStats();
                                    int j = Mth.clamp(serverstatscounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                                    int k = 24000;
                                    if (eventResult.isAllowed() || randomsource.nextInt(j) >= k * switch (difficultyGeneral) {
                                        case INSANE -> 2;
                                        case NIGHTMARE -> 1;
                                        default -> 3;
                                    }) {
                                        BlockPos blockpos1 = blockpos.above(20 + randomsource.nextInt(15))
                                                .east(-10 + randomsource.nextInt(21))
                                                .south(-10 + randomsource.nextInt(21));
                                        BlockState blockstate = p_64576_.getBlockState(blockpos1);
                                        FluidState fluidstate = p_64576_.getFluidState(blockpos1);
                                        if (NaturalSpawner.isValidEmptySpawnBlock(p_64576_, blockpos1, blockstate, fluidstate, EntityType.PHANTOM)) {
                                            SpawnGroupData spawngroupdata = null;
                                            int l = event.getPhantomsToSpawn();

                                            for (int i1 = 0; i1 < l; i1++) {
                                                Phantom phantom = EntityType.PHANTOM.create(p_64576_);
                                                if (phantom != null) {
                                                    phantom.moveTo(blockpos1, 0.0F, 0.0F);
                                                    spawngroupdata = phantom.finalizeSpawn(p_64576_, difficultyinstance, MobSpawnType.NATURAL, spawngroupdata);
                                                    p_64576_.addFreshEntityWithPassengers(phantom);
                                                    i++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return i;
                }
            }
        }
    }
}
