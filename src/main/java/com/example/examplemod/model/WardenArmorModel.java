package com.example.examplemod.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WardenArmorModel extends MyEnergySwirlLayer<Warden, WardenModel<Warden>> {

   // private static final ResourceLocation WARDEN_ARMOR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private static final ResourceLocation WARDEN_ARMOR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final WardenModel<Warden> model;
    public static ModelLayerLocation WARDEN_ARMOR = register("warden","armor");

    public WardenArmorModel(RenderLayerParent<Warden, WardenModel<Warden>> p_174554_, EntityModelSet p_174555_) {
        super(p_174554_);
        this.model = new WardenModel<>(p_174555_.bakeLayer(WARDEN_ARMOR));
    }

    @Override
    protected float xOffset(float p_117702_) {
        return Mth.cos(p_117702_ * 0.02F) * 3.0F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WARDEN_ARMOR_LOCATION;
    }

    @Override
    protected EntityModel<Warden> model() {
        return this.model;
    }
    private static ModelLayerLocation register(String p_171296_, String p_171297_) {
        ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
        return modellayerlocation;
    }

    private static ModelLayerLocation createLocation(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(ResourceLocation.withDefaultNamespace(p_171301_), p_171302_);
    }
}
