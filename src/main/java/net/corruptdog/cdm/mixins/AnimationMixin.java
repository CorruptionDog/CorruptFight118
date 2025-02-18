package net.corruptdog.cdm.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.*;
import yesman.epicfight.particle.EpicFightParticles;

import java.util.Random;

@Mixin(value = Animations.class, remap = false)
public class AnimationMixin {
    @Shadow
    public static StaticAnimation GREATSWORD_AUTO1;
    @Shadow
    public static StaticAnimation DANCING_EDGE;
    @Shadow
    public static StaticAnimation BATTOJUTSU_DASH;
    @Inject(at = @At("TAIL"), method = "build")
    private static void rebuild(CallbackInfo ci) {
        HumanoidArmature biped = Armatures.BIPED;

        GREATSWORD_AUTO1 = (new BasicAttackAnimation(0.3F, 0.15F, 0.55F, 0.65F, null, biped.toolR, "biped/combat/greatsword_auto1", biped)).addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F);
        BATTOJUTSU_DASH = new AttackAnimation(0.15F, "biped/skill/battojutsu_dash", biped,
                new AttackAnimation.Phase(0.0F, 0.55F, 0.8F, 0.95F, 1.0F,  biped.rootJoint,  ColliderPreset.BATTOJUTSU_DASH)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(5))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP),
                new AttackAnimation.Phase(1.0F, 1.0F, 1.3F, 1.4F, 1.4F, InteractionHand.MAIN_HAND, biped.rootJoint, ColliderPreset.BATTOJUTSU)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP))
                .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
                .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.05F, Animations.ReusableSources.PLAY_SOUND, AnimationEvent.Side.SERVER).params(EpicFightSounds.SWORD_IN),
                        AnimationEvent.TimeStampedEvent.create(0.7F, (entitypatch, animation, params) -> {
                            Entity entity = entitypatch.getOriginal();
                            entity.level.addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
                            Random random = entitypatch.getOriginal().getRandom();
                            double x = entity.getX() + (random.nextDouble() - random.nextDouble()) * 2.0D;
                            double y = entity.getY();
                            double z = entity.getZ() + (random.nextDouble() - random.nextDouble()) * 2.0D;
                            entity.level.addParticle(ParticleTypes.EXPLOSION, x, y, z, random.nextDouble() * 0.005D, 0.0D, 0.0D);
                        }, AnimationEvent.Side.CLIENT)
                );
        DANCING_EDGE = new AttackAnimation(0.1F, "biped/skill/dancing_edge", biped,
                new AttackAnimation.Phase(0.0F, 0.2F, 0.31F, 0.4F, 0.4F, biped.toolR, null),
                new AttackAnimation.Phase(0.4F, 0.5F, 0.61F, 0.65F, 0.65F, InteractionHand.OFF_HAND, biped.toolL, null),
                new AttackAnimation.Phase(0.65F, 0.75F, 0.85F, 1.15F, Float.MAX_VALUE, biped.toolR, null))
                .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
                .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);

    }
}

