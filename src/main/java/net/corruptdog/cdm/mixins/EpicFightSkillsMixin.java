package net.corruptdog.cdm.mixins;

import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Set;

@Mixin(value = EpicFightSkills.class,remap = false)
@Mod.EventBusSubscriber(modid= EpicFightMod.MODID)
public class EpicFightSkillsMixin {

    @Shadow
    public static Skill BLADE_RUSH;
    @Shadow
    public static Skill EVISCERATE;
    @Inject(method = "buildSkillEvent",at = @At("TAIL"))
    private static void buildSkillEvent(SkillBuildEvent onBuild, CallbackInfo ci) {
        WeaponInnateSkill eviscerate = onBuild.build(EpicFightMod.MODID, "eviscerate");
        eviscerate.newProperty()
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .newProperty()
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(), ExtraDamageInstance.TARGET_LOST_HEALTH.create(0.15F)))
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(50.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .registerPropertiesToAnimation();
        EVISCERATE = eviscerate;

        WeaponInnateSkill bladeRush = onBuild.build(EpicFightMod.MODID, "blade_rush");
        bladeRush.newProperty()
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .newProperty()
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.EXECUTION, SourceTags.WEAPON_INNATE))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER)
                .registerPropertiesToAnimation();
        BLADE_RUSH = bladeRush;
    }
}
