package com.example.examplemod.mixins.mobs;

import com.example.examplemod.goal.ShulkerGoal;
import com.example.examplemod.intrtfaces.IShulker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Shulker.class)
public class MixShulker extends AbstractGolem implements IShulker {

    public MixShulker(EntityType<? extends AbstractGolem> p_27508_, Level p_27509_) {
        super(p_27508_, p_27509_);
    }

    @Shadow
    boolean canStayAt(BlockPos p_149786_, Direction p_149787_) {
        return false;
    }

    @Shadow
    void setRawPeekAmount(int p_33419_) {

    }

    @Override
    public void setRawPeekAmountY(int amount) {
        setRawPeekAmount(amount);
    }

    @Override
    public boolean canStayAtY(BlockPos p_149786_, Direction p_149787_) {
        return canStayAt(p_149786_, p_149787_);
    }
    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F, 0.02F, true));
        this.goalSelector.addGoal(4, new ShulkerGoal.ShulkerAttackGoal((Shulker)(Object) this));
        this.goalSelector.addGoal(7, new ShulkerGoal.ShulkerPeekGoal((Shulker) (Object) this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, this.getClass()).setAlertOthers());
        this.targetSelector.addGoal(2, new ShulkerGoal.ShulkerNearestAttackGoal((Shulker) (Object) this));
        this.targetSelector.addGoal(3, new ShulkerGoal.ShulkerDefenseAttackGoal((Shulker) (Object) this));
    }
}
