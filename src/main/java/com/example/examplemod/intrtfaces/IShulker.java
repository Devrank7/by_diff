package com.example.examplemod.intrtfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IShulker {

    void setRawPeekAmountY(int amount);
    boolean canStayAtY(BlockPos p_149786_, Direction p_149787_);
}
