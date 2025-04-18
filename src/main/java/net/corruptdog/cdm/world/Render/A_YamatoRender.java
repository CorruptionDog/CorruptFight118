package net.corruptdog.cdm.world.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.corruptdog.cdm.world.item.CDAddonItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class A_YamatoRender extends RenderItemBase {

    private final ItemStack sheathStack = new ItemStack(CDAddonItems.A_YAMATO_SHEATH.get());
    private final ItemStack sheathStack2 = new ItemStack(CDAddonItems.A_YAMATO_IN_SHEATH.get());
    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        OpenMatrix4f modelMatrix = new OpenMatrix4f(this.mainhandcorrectionMatrix);
        modelMatrix.mulFront(poses[armature.toolR.getId()]);
        OpenMatrix4f modelMatrix_1 = new OpenMatrix4f(this.mainhandcorrectionMatrix);
        modelMatrix_1.mulFront(poses[armature.toolL.getId()]);

        DynamicAnimation animation = entitypatch.getAnimator().getPlayerFor(null).getAnimation();
        if (!animation.equals(CorruptAnimations.YAMATO_POWER0_1)
                && !animation.equals(CorruptAnimations.YAMATO_IDLE)
                && !animation.equals(CorruptAnimations.YAMATO_POWER0_2)
                && !animation.equals(Animations.BIPED_HIT_LONG)
                && !animation.equals(Animations.BIPED_HIT_SHORT)
                && !animation.equals(CorruptAnimations.YAMATO_RUN)
                && !animation.equals(CorruptAnimations.YAMATO_WALK)
                && !animation.equals(Animations.BIPED_STEP_BACKWARD)
                && !animation.equals(Animations.BIPED_STEP_FORWARD)
                && !animation.equals(Animations.BIPED_STEP_LEFT)
                && !animation.equals(Animations.BIPED_STEP_RIGHT)
                && !animation.equals(CorruptAnimations.GUARD_BREAK1)) {
            poseStack.pushPose();
            this.mulPoseStack(poseStack, modelMatrix);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer,  0);
            poseStack.popPose();
        }

        ItemStack currentSheath = (animation.equals(CorruptAnimations.YAMATO_AUTO2)
                || animation.equals(CorruptAnimations.YAMATO_AUTO3)
                || animation.equals(CorruptAnimations.YAMATO_AUTO4)
                || animation.equals(CorruptAnimations.YAMATO_AIRSLASH)
                || animation.equals(CorruptAnimations.YAMATO_DASH)
                || animation.equals(CorruptAnimations.YAMATO_POWER1)
                || animation.equals(CorruptAnimations.YAMATO_POWER2)
                || animation.equals(CorruptAnimations.YAMATO_POWER3)
                || animation.equals(CorruptAnimations.YAMATO_POWER3_REPEAT)
                || animation.equals(CorruptAnimations.YAMATO_POWER3_FINISH)
                || animation.equals(CorruptAnimations.YAMATO_POWER_DASH)
                || animation.equals(CorruptAnimations.YAMATO_STRIKE1)
                || animation.equals(CorruptAnimations.YAMATO_STRIKE2)
                || animation.equals(CorruptAnimations.YAMATO_RISING_SLASH)
                || animation.equals(CorruptAnimations.YAMATO_TURN_SLASH)
                || animation.equals(CorruptAnimations.YAMATO_TWIN_SLASH))
                ? sheathStack : sheathStack2;

        poseStack.pushPose();
        this.mulPoseStack(poseStack, modelMatrix_1);
        Minecraft.getInstance().getItemRenderer().renderStatic(currentSheath, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
        poseStack.popPose();
    }
}