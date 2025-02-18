package net.corruptdog.cdm.animations.types;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.gamerule.EpicFightGamerules;


public class DodgeAttackAnimation extends AttackAnimation {
    public static final Function<DamageSource, AttackResult.ResultType> DODGEABLE_SOURCE_VALIDATOR = (damagesource) -> {
        if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassArmor() && !damagesource.isBypassInvul()) {
            return AttackResult.ResultType.MISSED;
        }

        return AttackResult.ResultType.SUCCESS;
    };

    public DodgeAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);

        this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
        this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
        this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
    }
    @Override
    protected void bindPhaseState(Phase phase) {
        float preDelay = phase.preDelay;

        this.stateSpectrumBlueprint
                .newTimePair(phase.start, preDelay)
                .addState(EntityState.PHASE_LEVEL, 1)
                .newTimePair(phase.start, phase.contact)
                .addState(EntityState.CAN_BASIC_ATTACK, false)
                .addState(EntityState.CAN_SKILL_EXECUTION, false)

                .newTimePair(phase.start, phase.recovery)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .addState(EntityState.CAN_BASIC_ATTACK, false)

                .newTimePair(phase.start, phase.end)
                .addState(EntityState.INACTION, true)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .newTimePair(preDelay, phase.contact)
                .addState(EntityState.TURNING_LOCKED, true)
                .addState(EntityState.ATTACKING, true)
                .addState(EntityState.PHASE_LEVEL, 2)

                .newTimePair(phase.contact, phase.end)
                .addState(EntityState.PHASE_LEVEL, 3)
                .addState(EntityState.TURNING_LOCKED, true)

                .newTimePair(0.0F, Float.MAX_VALUE)
                .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR);
    }
    @Override
    protected void onLoaded() {
        super.onLoaded();

        if (!this.properties.containsKey(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
            float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", (1.0F / this.totalTime)));
            this.addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
        }
    }

    @Override
    public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
        if (layer == Layer.Priority.HIGHEST) {
            return !JointMaskEntry.BASIC_ATTACK_MASK.isMasked(entitypatch.getCurrentLivingMotion(), joint);
        } else {
            return super.isJointEnabled(entitypatch, layer, joint);
        }
    }
}