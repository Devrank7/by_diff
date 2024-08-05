package com.example.examplemod.model;

import com.example.examplemod.intrtfaces.IWarden;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;

public abstract class MyEnergySwirlLayer<T extends Warden, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public MyEnergySwirlLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(
            PoseStack p_116970_,
            MultiBufferSource p_116971_,
            int p_116972_,
            T p_116973_,
            float p_116974_,
            float p_116975_,
            float p_116976_,
            float p_116977_,
            float p_116978_,
            float p_116979_
    ) {
        if (((IWarden) (Object) p_116973_).isPowered()) {
            float f = (float) p_116973_.tickCount + p_116976_;
            EntityModel<T> entitymodel = this.model();
            entitymodel.prepareMobModel(p_116973_, p_116974_, p_116975_, p_116976_);
            this.getParentModel().copyPropertiesTo(entitymodel);
            VertexConsumer vertexconsumer = p_116971_.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
            entitymodel.setupAnim(p_116973_, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
            entitymodel.renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, -8355712);
        }

    }
    protected abstract float xOffset ( float p_116968_);

    protected abstract ResourceLocation getTextureLocation ();

    protected abstract EntityModel<T> model ();

}
