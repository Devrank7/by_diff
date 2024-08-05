package com.example.examplemod.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class MyGuardianAttackSelector implements Predicate<LivingEntity> {

    private final Guardian guardian;

    public MyGuardianAttackSelector(Guardian p_32879_) {
        this.guardian = p_32879_;
    }

    public boolean test(@Nullable LivingEntity p_32881_) {
        return (p_32881_ instanceof Player || p_32881_ instanceof Squid || p_32881_ instanceof Axolotl) && p_32881_.distanceToSqr(this.guardian) > 9.0D;
    }
}
