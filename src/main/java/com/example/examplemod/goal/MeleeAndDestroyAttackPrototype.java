package com.example.examplemod.goal;

import com.example.examplemod.intrtfaces.IPath;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MeleeAndDestroyAttackPrototype extends Goal {

    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected final boolean followingTargetEvenIfNotSeen;
    protected Path path;
    protected double pathedTargetX;
    protected double pathedTargetY;
    protected double pathedTargetZ;
    protected int ticksUntilNextPathRecalculation;
    protected int ticksUntilNextAttack;
    protected final int attackInterval;
    protected long lastCanUseCheck;
    protected static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    protected BlockPos blockToBreak;

    protected boolean blockIsBroken = false;
    protected int breakPrcocessTicks = 0;
    protected int lastBreakProgress = -1;

    protected BlockPos lastMemberPos = null;

    protected int unReachTickTimer = 0;
    protected boolean entryPoint = false;

    public MeleeAndDestroyAttackPrototype(PathfinderMob p_25552_, double p_25553_, boolean p_25554_, int p_25555) {
        this.mob = p_25552_;
        this.speedModifier = p_25553_;
        this.followingTargetEvenIfNotSeen = p_25554_;
        this.attackInterval = p_25555;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                lastMemberPos = null;
                return false;
            } else {
                this.path = ((IPath) this.mob.getNavigation()).createPathZ(ImmutableSet.of(target.blockPosition()), 16, false, 0);
                return this.path != null;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            lastMemberPos = null;
            return false;
        } else if (!this.followingTargetEvenIfNotSeen && !this.mob.getNavigation().isDone()) {
            return false;
        } else {
            return this.mob.isWithinRestriction(target.blockPosition()) &&
                    !(target instanceof Player && (((Player) target).isSpectator() || ((Player) target).isCreative()));
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        entryPoint = true;
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
        this.unReachTickTimer = 0;
    }

    @Override
    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setTarget(null);
        }
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            reachUpdate();
            lookAt(target);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (blockToBreak != null) {
                double distanceToBlock = this.mob.distanceToSqr(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ());
                if (distanceToBlock < 9.0) {
                    if (breakBlock(blockToBreak)) {
                        resetBlockToBreak();
                    } else {
                        checkTarget(target);
                    }
                } else {
                    mob.level().destroyBlockProgress(mob.getId(), blockToBreak, -1);
                    resetBlockToBreak();
                }
            } else {
                if (this.followingTargetEvenIfNotSeen &&
                        this.ticksUntilNextPathRecalculation <= 0 &&
                        (target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F) || blockIsBroken) {
                    blockIsBroken = false;
                    Path pathz = getPathZ(target);
                    checkDst(target, pathz);
                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
                }
            }
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(target);
        }
    }

    private void resetBlockToBreak() {
        blockToBreak = null;
        entryPoint = true;
        lastMemberPos = null;
        ticksUntilNextPathRecalculation = 0;
        blockIsBroken = true;
        breakPrcocessTicks = 0;
    }

    private void resetBlockToPath() {
        blockToBreak = null;
        lastMemberPos = null;
    }

    private void reachUpdate() {
        Path path = this.mob.getNavigation().getPath();
        boolean canReachEntityToTarget = path != null && path.canReach();
        if (!canReachEntityToTarget) {
            ++this.unReachTickTimer;
        } else {
            this.unReachTickTimer = 0;
        }
    }

    private void lookAt(LivingEntity target) {
        if (blockToBreak != null) {
            this.mob.getLookControl().setLookAt(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ(), 30.0F, 30.0F);
        } else {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }

    private void checkTarget(LivingEntity target) {
        Path path1 = ((IPath) this.mob.getNavigation()).createPathZ(ImmutableSet.of(target.blockPosition()), 16, false, 0);
        if (lastMemberPos == null || entryPoint) {
            lastMemberPos = target.blockPosition();
            entryPoint = false;
        }
        double dstSqrt = target.distanceToSqr(lastMemberPos.getX(), lastMemberPos.getY(), lastMemberPos.getZ());
        boolean canReachToTarget = path1 != null && path1.canReach();
        if (dstSqrt > 36.0D || (canReachToTarget)) {
            mob.level().destroyBlockProgress(mob.getId(), blockToBreak, -1);
            resetBlockToBreak();
        }
        if (blockToBreak != null) {
            Path absPath = this.mob.getNavigation().createPath(blockToBreak, 0);
            this.mob.getNavigation().moveTo(absPath, speedModifier);
        }
    }

    private void checkBlockPos(LivingEntity target, boolean canReachToTarget, double distanceToTarget) {
        if (blockToBreak != null) {
            double distanceToSqr = mob.distanceToSqr(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ());
            if (distanceToSqr > 9.0) {
                resetBlockToPath();
            }
        } else {
            double distanceToSqr = canReachToTarget ? distanceToTarget : mob.distanceToSqr(lastMemberPos.getX(), lastMemberPos.getY(), lastMemberPos.getZ());
            if (distanceToSqr > 9.0) {
                resetBlockToPath();
            } else {
                blockToBreak = target.getOnPos();
            }
        }
    }

    private void checkToDestroy() {
        double destroyTimeInTicks = blockDestroyTimeTicksOrSeconds(mob, blockToBreak, true);
        if (destroyTimeInTicks > 150) {
            resetBlockToBreak();
        }
    }

    private boolean canReachToTarget(LivingEntity target) {
        boolean canReachToTarget = unReachTickTimer < 40;
        if (lastMemberPos == null || canReachToTarget) {
            lastMemberPos = target.blockPosition();
        } else {
            double dstSqrt = target.distanceToSqr(lastMemberPos.getX(), lastMemberPos.getY(), lastMemberPos.getZ());
            if (dstSqrt > 36.0D) {
                lastMemberPos = target.blockPosition();
            }
        }
        return canReachToTarget;
    }

    private void checkDst(LivingEntity target, Path pathz) {
        double distanceToTarget = this.mob.distanceToSqr(target);
        if (distanceToTarget > 1024.0D) {
            checkNavigation(target, true);
        } else if (distanceToTarget > 256.0D) {
            checkNavigation(target, false);
        } else if (distanceToTarget < 256.0D) {
            if (pathz == null) {
                ticksUntilNextPathRecalculation += 1;
            } else {
                boolean canReachToTarget = canReachToTarget(target);
                if (!this.mob.getNavigation().moveTo(canReachToTarget ? pathz : mob.getNavigation().createPath(lastMemberPos, 0), speedModifier)) {
                    if (blockToBreak == null) {
                        BlockPos nodePos = (canReachToTarget ? pathz.getEndNode().asBlockPos() : mob.getNavigation().createPath(lastMemberPos, 0).getEndNode().asBlockPos());
                        blockToBreak = findObstructingOnNode(mob, nodePos, canReachToTarget ? target.blockPosition() : lastMemberPos, true);//findObstructingBlockToBreakWithoutPath(mob, canReachToTarget ? target.blockPosition() : lastMemberPos);
                        checkBlockPos(target, canReachToTarget, distanceToTarget);
                        checkToDestroy();
                    }
                }
            }
        }
    }

    private void checkNavigation(LivingEntity target, boolean isFar) {
        if (!this.mob.getNavigation().moveTo(target, this.speedModifier)) {
            this.ticksUntilNextPathRecalculation += 15;
        }
        this.ticksUntilNextPathRecalculation += isFar ? 10 : 5;
    }

    private Path getPathZ(LivingEntity target) {
        blockIsBroken = false;
        Path pathz = ((IPath) this.mob.getNavigation()).createPathZ(ImmutableSet.of(target.blockPosition()), 16, false, 0);
        this.pathedTargetX = target.getX();
        this.pathedTargetY = target.getY();
        this.pathedTargetZ = target.getZ();
        this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
        return pathz;
    }

    public static BlockPos findObstructingOnNode(PathfinderMob mob, BlockPos last_node, BlockPos target, boolean isAbove) {
        BlockPos bestPos = null;
        Map<BlockPos, Double> map = new HashMap<>();
        for (int i = 0; i <= 1; i++) {
            int pl = isAbove ? i : -i;
            double distanceToTarget = distSqr(target, last_node.atY(last_node.getY() + pl));
            for (int j = 0; j < Direction.values().length; j++) {
                Direction direction = Direction.from3DDataValue(j);
                BlockPos nodePos = last_node.atY(last_node.getY() + pl).relative(direction);
                BlockState blockState = mob.level().getBlockState(nodePos);
                if (!blockState.isPathfindable(PathComputationType.LAND)) {
                    double distanceToSqr = distSqr(target, nodePos);
                    if (distanceToSqr < distanceToTarget) {
                        distanceToTarget = distanceToSqr;
                        bestPos = nodePos;
                    } else {
                        map.put(nodePos, distanceToSqr);
                    }
                }
            }
        }
        bestPos = extraCheck(bestPos, map, target, last_node);
        return bestPos;
    }

    private static BlockPos extraCheck(BlockPos pos, Map<BlockPos, Double> map, BlockPos target, BlockPos last_node) {
        BlockPos bestPos = pos;
        if (bestPos == null) {
            double nearest = Double.MAX_VALUE;
            for (Map.Entry<BlockPos, Double> entry : map.entrySet()) {
                boolean isBehind = isBehind(last_node, target, entry.getKey());
                if (entry.getValue() < nearest && !isBehind) {
                    nearest = entry.getValue();
                    bestPos = entry.getKey();
                }
            }
        }
        return bestPos;
    }

    protected static boolean isBehind(BlockPos lastNode, BlockPos target, BlockPos pos) {
        double v1x = target.getX() - lastNode.getX();
        double v1y = target.getY() - lastNode.getY();
        double v1z = target.getZ() - lastNode.getZ();
        double v2x = pos.getX() - lastNode.getX();
        double v2y = pos.getY() - lastNode.getY();
        double v2z = pos.getZ() - lastNode.getZ();
        double dotProduct = v1x * v2x + v1y * v2y + v1z * v2z;
        return dotProduct < 0;
    }

    public static double distSqr(BlockPos b1, BlockPos b2) {
        return (b1.getX() - b2.getX()) * (b1.getX() - b2.getX()) + (b1.getY() - b2.getY()) * (b1.getY() - b2.getY()) + (b1.getZ() - b2.getZ()) * (b1.getZ() - b2.getZ());
    }

    protected boolean breakBlock(BlockPos blockPos) {
        double destroyTime = blockDestroyTimeTicksOrSeconds(this.mob, blockPos, true);
        this.breakPrcocessTicks++;
        int i = (int) ((float) this.breakPrcocessTicks / destroyTime * 10.0F);
        if (i != this.lastBreakProgress) {
            this.mob.level().destroyBlockProgress(this.mob.getId(), blockPos, i);
            if (!mob.swinging) {
                mob.swing(InteractionHand.MAIN_HAND);
            }
            lastBreakProgress = i;
        }
        if (breakPrcocessTicks >= destroyTime) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.level().destroyBlock(blockPos, true);
            breakPrcocessTicks = 0;
            lastBreakProgress = 0;
            return true;
        }
        return false;
    }

    protected static double blockDestroyTimeTicksOrSeconds(PathfinderMob mob, BlockPos pos, boolean isTicks) {
        if (pos == null) {
            return Double.MAX_VALUE;
        }
        BlockState blockState = mob.level().getBlockState(pos);
        float defaultDestroyTime = blockState.getBlock().defaultDestroyTime();
        double toolMultiplier = mob.getMainHandItem().getDestroySpeed(blockState);
        double destroyTime = ((defaultDestroyTime * 1.5D) / toolMultiplier);
        return isTicks ? (destroyTime * 20.0D) : destroyTime;
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(target);
        }
    }

    protected boolean canPerformAttack(LivingEntity target) {
        return this.ticksUntilNextAttack <= 0 && this.mob.distanceToSqr(target) <= this.getAttackReachSqr(target);
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.attackInterval;
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double) (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + target.getBbWidth());
    }

}
