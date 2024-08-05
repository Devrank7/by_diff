package com.example.examplemod.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieSmartAttackGoal extends MeleeAndDestroyAttackPrototype {
    private final Zombie zombie;
    private int raiseArmTicks;

    public ZombieSmartAttackGoal(Zombie p_25552_, double p_25553_, boolean p_25554_, int p_25555) {
        super(p_25552_, p_25553_, p_25554_, p_25555);
        this.zombie = p_25552_;
    }

    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    public void stop() {
        super.stop();
        this.zombie.setAggressive(false);
    }

    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        this.zombie.setAggressive(this.raiseArmTicks >= 5 && this.ticksUntilNextAttack < this.attackInterval / 2);

    }
}
