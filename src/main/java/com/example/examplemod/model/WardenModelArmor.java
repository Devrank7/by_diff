package com.example.examplemod.model;

import com.example.examplemod.intrtfaces.IWardenModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.spongepowered.asm.mixin.Unique;

public class WardenModelArmor {
    @Unique
    public static LayerDefinition createBodyLayer(CubeDeformation p_170526_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
                "body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F,p_170526_), PartPose.offset(0.0F, -21.0F, 0.0F)
        );
        partdefinition2.addOrReplaceChild(
                "right_ribcage",
                CubeListBuilder.create().texOffs(90, 11).addBox(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F,p_170526_),
                PartPose.offset(-7.0F, -2.0F, -4.0F)
        );
        partdefinition2.addOrReplaceChild(
                "left_ribcage",
                CubeListBuilder.create().texOffs(90, 11).mirror().addBox(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F,p_170526_).mirror(false),
                PartPose.offset(7.0F, -2.0F, -4.0F)
        );
        PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild(
                "head", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F,p_170526_), PartPose.offset(0.0F, -13.0F, 0.0F)
        );
        partdefinition3.addOrReplaceChild(
                "right_tendril",
                CubeListBuilder.create().texOffs(52, 32).addBox(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F,p_170526_),
                PartPose.offset(-8.0F, -12.0F, 0.0F)
        );
        partdefinition3.addOrReplaceChild(
                "left_tendril",
                CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F,p_170526_),
                PartPose.offset(8.0F, -12.0F, 0.0F)
        );
        partdefinition2.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(44, 50).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F,p_170526_),
                PartPose.offset(-13.0F, -13.0F, 1.0F)
        );
        partdefinition2.addOrReplaceChild(
                "left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F,p_170526_), PartPose.offset(13.0F, -13.0F, 1.0F)
        );
        partdefinition1.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(76, 48).addBox(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F,p_170526_),
                PartPose.offset(-5.9F, -13.0F, 0.0F)
        );
        partdefinition1.addOrReplaceChild(
                "left_leg", CubeListBuilder.create().texOffs(76, 76).addBox(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F,p_170526_), PartPose.offset(5.9F, -13.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
