package com.example.examplemod.mixins.raid;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.raids.RaiderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Raid.class)
public abstract class MixRaid {

    @Shadow
    private int groupsSpawned;

    @Shadow
    private float totalHealth;

    @Shadow
    protected abstract boolean shouldSpawnBonusGroup();

    @Shadow
    public abstract void setLeader(int p_37711_, Raider p_37712_);

    @Shadow
    public abstract void joinRaid(int p_37714_, Raider p_37715_, @Nullable BlockPos p_37716_, boolean p_37717_);

    @Shadow
    private Optional<BlockPos> waveSpawnPos;

    @Shadow
    public abstract void updateBossbar();

    @Shadow
    protected abstract void setDirty();

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    @Final
    @Mutable
    private int numGroups;

    @Shadow
    @Final
    private RandomSource random;

    @Inject(method = "<init>(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = false)
    public void onInit(int p_37692_, ServerLevel p_37693_, BlockPos p_37694_, CallbackInfo info) {
        numGroups = getNumGroupsY(((ILevel) p_37693_).getDifficultyGen());
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void spawnGroup(BlockPos p_37756_) {
        boolean flag = false;
        int i = this.groupsSpawned + 1;
        this.totalHealth = 0.0F;
        boolean flag1 = this.shouldSpawnBonusGroup();
        for (RaiderType raid$raidertype : RaiderType.VALUES) {
            int j = this.getDefaultNumSpawnsY(raid$raidertype, i, flag1) + this.getPotentialBonusSpawnsY(raid$raidertype, this.random, i, ((ILevel) level).getDifficultyGen(), flag1);
            int k = 0;

            for (int l = 0; l < j; ++l) {
                Raider raider = raid$raidertype.entityType.create(this.level);
                if (raider == null) {
                    break;
                }

                if (!flag && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    this.setLeader(i, raider);
                    flag = true;
                }

                this.joinRaid(i, raider, p_37756_, false);
                if (raid$raidertype.entityType == EntityType.RAVAGER) {
                    Raider raider1 = null;
                    if (i == this.getNumGroupsY(DifficultyGeneral.NORMAL)) {
                        raider1 = EntityType.PILLAGER.create(this.level);
                    } else if (i >= this.getNumGroupsY(DifficultyGeneral.HARD)) {
                        if (k == 0) {
                            raider1 = EntityType.EVOKER.create(this.level);
                        } else {
                            raider1 = EntityType.VINDICATOR.create(this.level);
                        }
                    } else if (i >= this.getNumGroupsY(DifficultyGeneral.INSANE)) {
                        switch (k) {
                            case 0 -> raider1 = EntityType.EVOKER.create(this.level);
                            case 1 -> raider1 = EntityType.VINDICATOR.create(this.level);
                            case 2 -> raider1 = EntityType.ILLUSIONER.create(this.level);
                        }
                    }
                    ++k;
                    if (raider1 != null) {
                        this.joinRaid(i, raider1, p_37756_, false);
                        raider1.moveTo(p_37756_, 0.0F, 0.0F);
                        raider1.startRiding(raider);
                    }
                }
            }
        }

        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }

    @Unique
    private int getDefaultNumSpawnsY(RaiderType p_37731_, int p_37732_, boolean p_37733_) {
        return p_37733_ ? p_37731_.spawnsPerWaveBeforeBonus[this.numGroups] : p_37731_.spawnsPerWaveBeforeBonus[p_37732_];
    }

    @Unique
    private int getPotentialBonusSpawnsY(RaiderType p_219829_, RandomSource p_219830_, int p_219831_, DifficultyGeneral p_219832_, boolean p_219833_) {
        boolean easy = p_219832_ == DifficultyGeneral.EASY;
        boolean normal = p_219832_ == DifficultyGeneral.NORMAL;
        boolean hard = p_219832_ == DifficultyGeneral.HARD;
        boolean insane = p_219832_ == DifficultyGeneral.INSANE;
        boolean nightmare = p_219832_ == DifficultyGeneral.NIGHTMARE;
        int i;
        switch (p_219829_) {
            case WITCH:
                if (easy || p_219831_ <= 2 || p_219831_ == 4) {
                    return 0;
                }

                i = switch (p_219832_) {
                    case NORMAL, HARD -> 1;
                    case INSANE -> 2;
                    case NIGHTMARE -> random.nextBoolean() ? 3 : 4;
                    default -> 0;
                };
                break;
            case PILLAGER:
            case VINDICATOR:
                i = 0;
                if (easy) {
                    i = random.nextInt(2);
                } else if (normal) {
                    i = 1;
                } else if (hard) {
                    i = 2;
                } else if (insane) {
                    i = random.nextBoolean() ? 3 : 4;
                } else if (nightmare) {
                    i = random.nextBoolean() ? random.nextBoolean() ? 5 : 4 : 6;
                }
                break;
            case RAVAGER:
                i = switch (p_219832_) {
                    case INSANE -> {
                        if (p_219833_) {
                            yield 1;
                        }
                        yield random.nextInt(2);
                    }
                    case NIGHTMARE -> {
                        if (p_219833_) {
                            yield 2;
                        }
                        yield random.nextInt(1, 3);
                    }
                    default -> !easy && p_219833_ ? 1 : 0;
                };
                break;
            default:
                return 0;
        }

        return i > 0 ? p_219830_.nextInt(i + 1) : 0;
    }

    public int getNumGroupsY(DifficultyGeneral p_37725_) {
        return switch (p_37725_) {
            case EASY -> 3;
            case NORMAL -> 5;
            case HARD -> 7;
            case INSANE -> 9;
            case NIGHTMARE -> 11;
            default -> 0;
        };
    }
}
