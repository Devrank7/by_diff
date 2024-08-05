package com.example.examplemod.mixins.mobs;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.goal.NearestTargetWithoutSeenGoal;
import com.example.examplemod.goal.ZombieSmartAttackGoal;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Mixin(Zombie.class)
public class MixZombie extends Monster {
    @Shadow
    @Final
    private static ResourceLocation REINFORCEMENT_CALLER_CHARGE_ID;
    @Shadow
    @Final
    private static AttributeModifier ZOMBIE_REINFORCEMENT_CALLEE_CHARGE;

    public MixZombie(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void addBehaviourGoals() {
        Zombie zombie = (Zombie) (Object) this;
        this.goalSelector.addGoal(2, new ZombieSmartAttackGoal(zombie, 1.0D, true, 10));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(zombie, 1.0D, true, 4, zombie::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(zombie, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(zombie)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestTargetWithoutSeenGoal<>(zombie, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(zombie, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(zombie, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(zombie, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    protected void populateDefaultEquipmentSlots(RandomSource p_219165_, DifficultyInstance p_219166_) {
        super.populateDefaultEquipmentSlots(p_219165_, p_219166_);
        DifficultyGeneral lev_diff = ((ILevel) this.level()).getDifficultyGen();
        float chance = switch (lev_diff) {
            case HARD -> 0.05F;
            case INSANE -> 0.25F;
            case NIGHTMARE -> 0.55f;
            default -> 0.01F;
        };
        if (p_219165_.nextFloat() < (float) chance) {
            int i = p_219165_.nextInt(3);
            if (i == 0) {
                float f = switch (lev_diff) {
                    case INSANE -> 0.200f;
                    case NIGHTMARE -> 0.450f;
                    default -> 0.001f;
                };
                boolean flag = p_219165_.nextFloat() < f;
                this.setItemSlot(EquipmentSlot.MAINHAND, flag ? new ItemStack(Items.DIAMOND_SWORD) : new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"), remap = false)
    public void finalizeSpawn(ServerLevelAccessor p_34297_, DifficultyInstance p_34298_, MobSpawnType p_34299_, @Nullable SpawnGroupData p_34300_, CallbackInfoReturnable<SpawnGroupData> cir) {
        DifficultyGeneral difficult = ((ILevel) level()).getDifficultyGen();
        boolean flag = difficult == DifficultyGeneral.NIGHTMARE || difficult == DifficultyGeneral.INSANE;
        boolean flag1 = random.nextFloat() < (difficult == DifficultyGeneral.NIGHTMARE ? 0.25F : 0.1F);

        if (!flag && !flag1) {
            WrappedGoal breakGoal = goalSelector.getAvailableGoals().stream().filter(goal -> goal.getGoal() instanceof ZombieSmartAttackGoal).findFirst().orElse(null);
            if (breakGoal != null) {
                goalSelector.removeGoal(breakGoal.getGoal());
                goalSelector.addGoal(2, new ZombieAttackGoal((Zombie) (Object) this, 1.0D, true));
            }
            WrappedGoal attackGoal = targetSelector.getAvailableGoals().stream().filter(goal -> goal.getGoal() instanceof NearestTargetWithoutSeenGoal<?>).findFirst().orElse(null);
            if (attackGoal != null) {
                targetSelector.removeGoal(attackGoal.getGoal());
                targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((Zombie) (Object) this, Player.class, true));
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite(remap = false)
    public boolean hurt(DamageSource p_34288_, float p_34289_) {
        if (!super.hurt(p_34288_, p_34289_)) {
            return false;
        } else if (!(this.level() instanceof ServerLevel)) {
            return false;
        } else {
            ServerLevel serverlevel = (ServerLevel) this.level();
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && p_34288_.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity) p_34288_.getEntity();
            }
            DifficultyGeneral difficulty = ((ILevel) this.level()).getDifficultyGen();
            float spawn_rainforcement_Chance = switch (difficulty) {
                case INSANE -> 0.2f;
                case NIGHTMARE -> 0.4f;
                default -> 0.05f;
            };

            var vanilla = (livingentity != null
                    && (difficulty == DifficultyGeneral.HARD || difficulty == DifficultyGeneral.INSANE || difficulty == DifficultyGeneral.NIGHTMARE)
                    && (double) this.random.nextFloat() < spawn_rainforcement_Chance
                    && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING));

            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ());

            var event = net.minecraftforge.event.ForgeEventFactory.fireZombieSummonAid((Zombie)(Object) this, level(), i, j, k, livingentity, this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE));

            Zombie zombie = null;
            if (event.getResult().isAllowed()) {
                zombie = event.getCustomSummonedAid() != null ? event.getCustomSummonedAid() : EntityType.ZOMBIE.create(this.level());
            } else if (vanilla && event.getResult().isDefault()) {
                zombie = EntityType.ZOMBIE.create(this.level());
            }

            if (zombie != null) {
                for (int l = 0; l < 50; l++) {
                    int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entitytype = zombie.getType();
                    if (SpawnPlacements.isSpawnPositionOk(entitytype, this.level(), blockpos)
                            && SpawnPlacements.checkSpawnRules(entitytype, serverlevel, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) {
                        zombie.setPos((double) i1, (double) j1, (double) k1);
                        if (!this.level().hasNearbyAlivePlayer((double) i1, (double) j1, (double) k1, 7.0)
                                && this.level().isUnobstructed(zombie)
                                && this.level().noCollision(zombie)
                                && !this.level().containsAnyLiquid(zombie.getBoundingBox())) {
                            if (livingentity != null)
                                zombie.setTarget(livingentity);
                            zombie.finalizeSpawn(serverlevel, this.level().getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.REINFORCEMENT, null);
                            serverlevel.addFreshEntityWithPassengers(zombie);
                            AttributeInstance attributeinstance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                            AttributeModifier attributemodifier = attributeinstance.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            double d0 = attributemodifier != null ? attributemodifier.amount() : 0.0;
                            attributeinstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            attributeinstance.addPermanentModifier(new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, d0 - 0.05, AttributeModifier.Operation.ADD_VALUE));
                            zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(ZOMBIE_REINFORCEMENT_CALLEE_CHARGE);
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }
}
