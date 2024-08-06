package com.example.examplemod.mixins.boss.renders;

import com.example.examplemod.model.WardenArmorModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.WardenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WardenRenderer.class)
@OnlyIn(Dist.CLIENT)
public class MixWardenRender extends MobRenderer<Warden, WardenModel<Warden>> {
    public MixWardenRender(EntityRendererProvider.Context p_174304_, WardenModel<Warden> p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void onInit(EntityRendererProvider.Context p_234787_, CallbackInfo info) {
        this.addLayer(new WardenArmorModel(this, p_234787_.getModelSet()));
    }

    @Override
    @Shadow
    public ResourceLocation getTextureLocation(Warden p_114482_) {
        return null;
    }
}
