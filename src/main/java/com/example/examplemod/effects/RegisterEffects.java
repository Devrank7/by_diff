package com.example.examplemod.effects;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterEffects {

    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            ExampleMod.MODID);
    public static final RegistryObject<MobEffect> EFFECT_REGISTRY_OOZING = EFFECT.register("oozing_diff",
            () -> new DifficultyOozingMobEffect(MobEffectCategory.HARMFUL,9356754,p_326759_ -> 2));
    public static final RegistryObject<MobEffect> EFFECT_REGISTRY_INFESTED = EFFECT.register("infested_diff",
            () -> new DifficultyInstedMobEffects(MobEffectCategory.HARMFUL,5356754,0.1f,p_326759_ -> 2));
}
