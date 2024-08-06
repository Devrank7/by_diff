package com.example.examplemod.goal;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShootAtGoal extends Goal {

    private AbstractSkeleton mob;
    private int maxDistance;
    private Class<? extends Block> blockClass;

    public Class<? extends Block> getBlockClass() {
        return blockClass;
    }

    public void setBlockClass(Class<? extends Block> blockClass) {
        this.blockClass = blockClass;
    }

    private BlockPos glassBlockPos;

    private int strengthSkileton;

    public int getStrengthSkileton() {
        return strengthSkileton;
    }

    public void setStrengthSkileton(int strengthSkileton) {
        this.strengthSkileton = strengthSkileton;
    }

    private int rightShoots = 0;

    private final double speedModifier;
    private int attackIntervalMin;

    public int getAttackIntervalMin() {
        return attackIntervalMin;
    }

    public void setAttackIntervalMin(int attackIntervalMin) {
        this.attackIntervalMin = attackIntervalMin;
    }

    private final float attackRadiusSqr;
    private int attackTime = -1;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;


    public ShootAtGoal(AbstractSkeleton mob, Class<? extends Block> blockClass, int strengthSkileton, int maxDistance, double speedModifier, int attackIntervalMin, float attackRadiusSqr) {
        this.maxDistance = maxDistance;
        this.mob = mob;
        this.blockClass = blockClass;
        this.strengthSkileton = strengthSkileton;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadiusSqr * attackRadiusSqr;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() != null) {
            return false;
        } else {
            glassBlockPos = findNearestGlass();
            return glassBlockPos != null;
        }
    }

    @Override
    public void start() {
        super.start();
        rightShoots = 0;
        this.mob.setAggressive(true);
        System.out.println("SHOOTING AT GLASS");
    }

    @Override
    public void stop() {
        rightShoots = 0;
        this.mob.setAggressive(false);
        this.attackTime = -1;
        this.mob.stopUsingItem();
        System.out.println("STOPPED SHOOTING AT GLASS");
        super.stop();
    }

    @Override
    public boolean canContinueToUse() {
        BlockState blockState = mob.level().getBlockState(glassBlockPos);
        return (blockClass.isInstance(blockState.getBlock())) && canSeeGlass(glassBlockPos) && !isFar();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos blockPos = glassBlockPos;
        if (blockPos != null) {
            double d0 = this.mob.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            if (!(d0 > (double) this.attackRadiusSqr)) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
                this.strafingTime = -1;
            }
            //System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");

            if (this.strafingTime >= 20) {
                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                Entity entity = this.mob.getControlledVehicle();
                if (entity instanceof Mob mob1) {
                    lookAt(mob1, blockPos, 30.0F, 30.0F);
                }

                lookAt(mob, blockPos, 30.0F, 30.0F);
            } else {
                this.mob.getLookControl().setLookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 30.0F, 30.0F);
            }

            if (this.mob.isUsingItem()) {
                int i = this.mob.getTicksUsingItem();
                if (i >= 20) {
                    this.mob.stopUsingItem();
                    performRangedAttack(mob, blockPos, BowItem.getPowerForTime(i));
                    this.attackTime = this.attackIntervalMin;
                }
            } else if (--this.attackTime <= 0) {
                this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
            }
        }

    }

    private BlockPos findNearestGlass() {
        BlockPos startPos = mob.blockPosition();
        double nearestDistance = Double.MAX_VALUE;
        BlockPos nearestBlock = null;
        int maxY = 3;
        for (int dx = -maxDistance; dx <= maxDistance; dx++) {
            for (int dy = -maxY; dy <= maxY; dy++) {
                for (int dz = -maxDistance; dz <= maxDistance; dz++) {
                    BlockPos currentPos = startPos.offset(dx, dy, dz);
                    BlockState blockState = mob.level().getBlockState(currentPos);
                    //System.out.println("HHH = " + blockState.getBlock().getName());
                    if (blockClass.isInstance(blockState.getBlock())) {
                        double distance = startPos.distSqr(currentPos);
                        if (distance < nearestDistance && canSeeGlass(currentPos)) {
                            System.out.println("NEAREST DISTANCE: " + distance);
                            nearestBlock = currentPos;
                            nearestDistance = distance;
                        }
                    }
                }
            }
        }
        return nearestBlock;
    }

    private boolean canSeeGlass(BlockPos pos) {
        Vec3 startVec = mob.position().add(0.0, mob.getEyeHeight(), 0.0);
        Vec3 endVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        BlockHitResult result = mob.level().clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        // Проверяем, что трассировка луча не попала в блоки на пути к цели
        return result.getType() == HitResult.Type.MISS || result.getBlockPos().equals(pos);
    }

    public void lookAt(Mob mob, BlockPos pos, float deltaYaw, float deltaPitch) {
        Vec3 targetVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, targetVec);
    }

    private float rotlerp(float p_21377_, float p_21378_, float p_21379_) {
        float f = Mth.wrapDegrees(p_21378_ - p_21377_);
        if (f > p_21379_) {
            f = p_21379_;
        }

        if (f < -p_21379_) {
            f = -p_21379_;
        }

        return p_21377_ + f;
    }

    public void performRangedAttack(AbstractSkeleton skeleton, BlockPos targetPos, float velocity) {
        ItemStack itemstack = skeleton.getItemInHand(ProjectileUtil.getWeaponHoldingHand(skeleton, item -> item instanceof net.minecraft.world.item.BowItem));
        ItemStack itemstack1 = skeleton.getProjectile(itemstack);
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(skeleton, itemstack1, velocity, itemstack);
        if (skeleton.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem bow) {
            abstractarrow = bow.customArrow(abstractarrow);
        }

        double d0 = targetPos.getX() + 0.5 - skeleton.getX();
        double d1 = targetPos.getY() - abstractarrow.getY() - 0.5f;
        double d2 = targetPos.getZ() + 0.5 - skeleton.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        DifficultyGeneral difficult = ((ILevel) skeleton.level()).getDifficultyGen();
        int i = switch (difficult) {
            case EASY -> 4;
            case NORMAL -> 8;
            case HARD -> 12;
            case INSANE, NIGHTMARE -> 14;
            default -> 0;
        };
        abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, (14 - i));
        skeleton.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (skeleton.getRandom().nextFloat() * 0.4F + 0.8F));
        skeleton.level().addFreshEntity(abstractarrow);
    }

    private boolean isFar() {
        double distanceSqr = mob.blockPosition().distSqr(glassBlockPos);
        // Пороговое значение квадрата расстояния для 20 блоков
        double maxDistanceSqr = attackRadiusSqr * 1.75D;

        // Возвращаем true, если расстояние больше или равно 20 блокам
        return distanceSqr >= maxDistanceSqr;
    }


    public void incrementRightShots() {
        rightShoots++;
    }

    public int getRightShoots() {
        return rightShoots;
    }

    public void setRightShoots(int rightShoots) {
        this.rightShoots = rightShoots;
    }
}
