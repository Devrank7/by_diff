package com.example.examplemod.mixins.boss;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.world.entity.ai.behavior.warden.SonicBoom.setCooldown;

@Mixin(SonicBoom.class)
public class MixSonicBoom extends Behavior<Warden> {


    @Shadow
    @Final
    private static double KNOCKBACK_VERTICAL;

    @Shadow
    @Final
    private static double KNOCKBACK_HORIZONTAL;

    @Shadow
    @Final
    private static int DURATION;

    @Shadow
    @Final
    private static int TICKS_BEFORE_PLAYING_SOUND;

    public MixSonicBoom() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED, MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED), DURATION);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    protected boolean checkExtraStartConditions(ServerLevel p_217692_, Warden p_217693_) {
        DifficultyGeneral my_difficulty = ((ILevel) p_217692_).getDifficultyGen();
        float mult = switch (my_difficulty) {
            case INSANE -> 1.4f;
            case NIGHTMARE -> 1.8f;
            default -> 1.0f;
        };
        return p_217693_.closerThan(p_217693_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D * mult, 20.0D * mult);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void tick(ServerLevel p_217724_, Warden p_217725_, long p_217726_) {
        p_217725_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((p_296749_) -> {
            p_217725_.getLookControl().setLookAt(p_296749_.position());
        });
        DifficultyGeneral my_difficulty = ((ILevel) p_217724_).getDifficultyGen();
        float mult = switch (my_difficulty) {
            case INSANE -> 1.4f;
            case NIGHTMARE -> 1.8f;
            default -> 1.0f;
        };

        if (!p_217725_.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) && !p_217725_.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
            p_217725_.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long) (DURATION - TICKS_BEFORE_PLAYING_SOUND));
            p_217725_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(p_217725_::canTargetEntity).filter((p_217707_) -> {
                return p_217725_.closerThan(p_217707_, 15.0D * mult, 20.0D * mult);
            }).ifPresent((p_217704_) -> {
                Vec3 vec3 = p_217725_.position().add(0.0D, (double) 1.6F, 0.0D);
                Vec3 vec31 = p_217704_.getEyePosition().subtract(vec3);
                Vec3 vec32 = vec31.normalize();

                for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
                    Vec3 vec33 = vec3.add(vec32.scale((double) i));
                    p_217724_.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                p_217725_.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                double damage = 10.0F;
                double knockbackVertical = KNOCKBACK_VERTICAL;
                double knockbackHorizontal = KNOCKBACK_HORIZONTAL;
                damage *= mult;

                p_217704_.hurt(p_217724_.damageSources().sonicBoom(p_217725_), (float) damage);
                double d1 = knockbackVertical * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double d0 = knockbackHorizontal * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                p_217704_.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
            });
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void stop(ServerLevel p_217732_, Warden p_217733_, long p_217734_) {
        DifficultyGeneral my_difficulty = ((ILevel) p_217732_).getDifficultyGen();
        int cooldown = switch (my_difficulty) {
            case INSANE -> 30;
            case NIGHTMARE -> 18;
            default -> 40;
        };
        setCooldown(p_217733_, cooldown);
    }
}
