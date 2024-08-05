package com.example.examplemod.mixins.block;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FireBlock.class)
public class MixFireBlock extends BaseFireBlock {
    @Shadow
    @Final
    public static IntegerProperty AGE;

    public MixFireBlock(Properties p_49241_, float p_49242_) {
        super(p_49241_, p_49242_);
    }

    @Override
    @Shadow
    protected MapCodec<? extends BaseFireBlock> codec() {
        return null;
    }

    @Override
    @Shadow
    protected boolean canBurn(BlockState p_49284_) {
        return false;
    }

    @Shadow
    private boolean isValidFireLocation(BlockGetter p_53486_, BlockPos p_53487_) {
        return false;
    }

    @Shadow(remap = false)
    private void checkBurnOut(Level p_221151_, BlockPos p_221152_, int p_221153_, RandomSource p_221154_, int p_221155_, Direction face) {
    }

    @Shadow
    protected boolean isNearRain(Level p_53429_, BlockPos p_53430_) {
        return false;
    }

    @Shadow
    private int getIgniteOdds(LevelReader p_221157_, BlockPos p_221158_) {
        return 0;
    }

    @Shadow
    private BlockState getStateWithAge(LevelAccessor p_53438_, BlockPos p_53439_, int p_53440_) {
        return null;
    }

    @Shadow
    private static int getFireTickDelay(RandomSource p_221149_) {
        return 0;
    }

    @Shadow(remap = false)
    public boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    protected void tick(BlockState p_221160_, ServerLevel p_221161_, BlockPos p_221162_, RandomSource p_221163_) {
        p_221161_.scheduleTick(p_221162_, this, getFireTickDelay(p_221161_.random));
        if (p_221161_.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!p_221160_.canSurvive(p_221161_, p_221162_)) {
                p_221161_.removeBlock(p_221162_, false);
            }

            BlockState blockstate = p_221161_.getBlockState(p_221162_.below());
            boolean flag = blockstate.isFireSource(p_221161_, p_221162_, Direction.UP);
            int i = p_221160_.getValue(AGE);
            if (!flag && p_221161_.isRaining() && this.isNearRain(p_221161_, p_221162_) && p_221163_.nextFloat() < 0.2F + (float) i * 0.03F) {
                p_221161_.removeBlock(p_221162_, false);
            } else {
                int j = Math.min(15, i + p_221163_.nextInt(3) / 2);
                if (i != j) {
                    p_221160_ = p_221160_.setValue(AGE, Integer.valueOf(j));
                    p_221161_.setBlock(p_221162_, p_221160_, 4);
                }

                if (!flag) {
                    if (!this.isValidFireLocation(p_221161_, p_221162_)) {
                        BlockPos blockpos = p_221162_.below();
                        if (!p_221161_.getBlockState(blockpos).isFaceSturdy(p_221161_, blockpos, Direction.UP) || i > 3) {
                            p_221161_.removeBlock(p_221162_, false);
                        }

                        return;
                    }

                    if (i == 15 && p_221163_.nextInt(4) == 0 && !this.canCatchFire(p_221161_, p_221162_.below(), Direction.UP)) {
                        p_221161_.removeBlock(p_221162_, false);
                        return;
                    }
                }

                boolean flag1 = p_221161_.getBiome(p_221162_).is(BiomeTags.INCREASED_FIRE_BURNOUT);
                int k = flag1 ? -50 : 0;
                this.checkBurnOut(p_221161_, p_221162_.east(), 300 + k, p_221163_, i, Direction.WEST);
                this.checkBurnOut(p_221161_, p_221162_.west(), 300 + k, p_221163_, i, Direction.EAST);
                this.checkBurnOut(p_221161_, p_221162_.below(), 250 + k, p_221163_, i, Direction.UP);
                this.checkBurnOut(p_221161_, p_221162_.above(), 250 + k, p_221163_, i, Direction.DOWN);
                this.checkBurnOut(p_221161_, p_221162_.north(), 300 + k, p_221163_, i, Direction.SOUTH);
                this.checkBurnOut(p_221161_, p_221162_.south(), 300 + k, p_221163_, i, Direction.NORTH);
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int l = -1; l <= 1; l++) {
                    for (int i1 = -1; i1 <= 1; i1++) {
                        for (int j1 = -1; j1 <= 4; j1++) {
                            if (l != 0 || j1 != 0 || i1 != 0) {
                                int k1 = 100;
                                if (j1 > 1) {
                                    k1 += (j1 - 1) * 100;
                                }

                                blockpos$mutableblockpos.setWithOffset(p_221162_, l, j1, i1);
                                int l1 = this.getIgniteOdds(p_221161_, blockpos$mutableblockpos);
                                if (l1 > 0) {
                                    DifficultyGeneral difficultyGen = ((ILevel) p_221161_).getDifficultyGen();
                                    int h = switch (difficultyGen) {
                                        case INSANE -> difficultyGen.getId() + 1;
                                        case NIGHTMARE -> difficultyGen.getId() + 3;
                                        default -> difficultyGen.getId();
                                    };
                                    int i2 = (l1 + 40 + h * 7) / (i + 30);
                                    if (flag1) {
                                        i2 /= 2;
                                    }

                                    if (i2 > 0
                                            && p_221163_.nextInt(k1) <= i2
                                            && (!p_221161_.isRaining() || !this.isNearRain(p_221161_, blockpos$mutableblockpos))) {
                                        int j2 = Math.min(15, i + p_221163_.nextInt(5) / 4);
                                        p_221161_.setBlock(blockpos$mutableblockpos, this.getStateWithAge(p_221161_, blockpos$mutableblockpos, j2), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
