package net.corruptdog.cdm.animations.types;

import java.util.function.Function;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityDimensions;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPRotateEntityModelYRot;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.DodgeLeft;

public class YamatoDodge extends ActionAnimation {
    private SkillContainer container;
    public static final Function<DamageSource, AttackResult.ResultType> DODGEABLE_SOURCE_VALIDATOR = (damagesource) -> {
        if (damagesource instanceof EntityDamageSource
                && !damagesource.isBypassArmor()
                && !damagesource.isBypassInvul()) {
            return AttackResult.ResultType.MISSED;
        }

        return AttackResult.ResultType.SUCCESS;
    };
    public YamatoDodge(float convertTime, float delayTime, String path, float width, float height, Armature armature) {
        super(convertTime, delayTime, path, armature);

        this.stateSpectrumBlueprint.clear()
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addState(EntityState.TURNING_LOCKED, true)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .addState(EntityState.CAN_BASIC_ATTACK, false)
                .addState(EntityState.CAN_SKILL_EXECUTION, false)
                .addState(EntityState.INACTION, true)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR);

        this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
        this.addEvents(StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH));
        this.addEvents(StaticAnimationProperty.EVENTS, AnimationEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(width, height)));
    }

    @Override
    public void begin(LivingEntityPatch<?> entitypatch) {
        super.begin(entitypatch);

        if (!entitypatch.isLogicalClient() && entitypatch != null) {
            entitypatch.getOriginal().level.addFreshEntity(new DodgeLeft(entitypatch));
        }
    }
    public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entitypatch, nextAnimation, isEnd);
        if (entitypatch.isLogicalClient() && entitypatch instanceof LocalPlayerPatch) {
            ((LocalPlayerPatch)entitypatch).changeModelYRot(0.0F);
            EpicFightNetworkManager.sendToServer(new CPRotateEntityModelYRot(0.0F));
        }

    }
}