package net.corruptdog.cdm.gameasset;

import java.util.Set;

import net.corruptdog.cdm.main.CDmoveset;
import net.corruptdog.cdm.skill.Dodge.WolfDodge;
import net.corruptdog.cdm.skill.weaponinnate.*;
import net.corruptdog.cdm.world.item.CDAddonItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.StepSkill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;

public class CDSkills {
    public static Skill SWORD_SLASH;
    public static Skill DUAL_SLASH;
    public static Skill SPEAR_SLASH;
    public static Skill LETHAL_SLICING;
    public static Skill YAMATOSKILL;
    public static Skill GUARDPARRY;
    public static Skill DUAL_GREATSWORD_SKILL;
    public static Skill WIND_SLASH;
    public static Skill FATAL_DRAW_DASH;
    public static Skill BLADE_RUSH_FINISHER;
    public static Skill KATANASPSKILL;
    public static Skill BLOODWOLF;
    public static Skill BLADE_RUSH;
    public static Skill YAMATO_STEP;
    public static Skill SSTEP;
    public static Skill BSTEP;
    public static Skill WOLF_DODGE;
    public static Skill GREAT_TACHISKILL;
    public static Skill KATANASKILL;
    public static Skill SHILEDSLASH;
    public static Skill FATALFLASH;

    public static void registerSkills() {
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/sword_slash")), CDmoveset.MOD_ID, "sword_slash");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dual_slash")), CDmoveset.MOD_ID, "dual_slash");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/spear_slash")), CDmoveset.MOD_ID, "spearslash");
        SkillManager.register(LethalSlicingSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "lethalslicing");
        SkillManager.register(YamatoSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "yamatoskill");
        SkillManager.register(GuardParrySkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(Skill.ActivateType.DURATION_INFINITE), CDmoveset.MOD_ID, "guardparry");
        SkillManager.register(DualTchiSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "greeat_tachiskill");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/wind_slash")), CDmoveset.MOD_ID, "wind_slash");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/katana/skill/fatal_draw")), CDmoveset.MOD_ID, "fatal_draw");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/blade_rush_finisher")), CDmoveset.MOD_ID, "blade_rush_finisher");
        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dual_greatsword/skill/greatsword_dual_earthquake")), CDmoveset.MOD_ID, "greatsword_dual_skill");
        SkillManager.register(TachiDual::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "katanaspskill");
        SkillManager.register(StepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/yamato_step_forward"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/yamato_step_backward"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/yamato_step_left"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/yamato_step_right")).setCreativeTab(CDAddonItems.CREATIVE_MODE_TAB), CDmoveset.MOD_ID, "yamato_step");
        SkillManager.register(StepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dodge/step_forward"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dodge/step_backward"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dodge/step_left"), new ResourceLocation(CDmoveset.MOD_ID, "biped/new/dodge/step_right")).setCreativeTab(CDAddonItems.CREATIVE_MODE_TAB), CDmoveset.MOD_ID, "sstep");
        SkillManager.register(KatanaSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "katanaskill");
        SkillManager.register(ShiledSlash::new, WeaponInnateSkill.createWeaponInnateBuilder(), CDmoveset.MOD_ID, "shiled_slahs");

        SkillManager.register(WolfDodge::new, WolfDodge.createDodgeBuilder()
                        .setAnimations1(
                                () -> CorruptAnimations.WOLFDODGE_FORWARD,
                                () -> CorruptAnimations.WOLFDODGE_BACKWARD,
                                () -> CorruptAnimations.STEP_LEFT,
                                () -> CorruptAnimations.STEP_RIGHT)
                        .setAnimations2(
                                () -> CorruptAnimations.ROLL_FORWARD,
                                () -> CorruptAnimations.ROLL_BACKWARD,
                                () -> CorruptAnimations.ROLL_LEFT,
                                () -> CorruptAnimations.ROLL_RIGHT)
                        .setAnimations3(
                                () -> CorruptAnimations.WOLFDODGE_FORWARD,
                                () -> CorruptAnimations.WOLFDODGE_BACKWARD,
                                () -> CorruptAnimations.STEP_LEFT,
                                () -> CorruptAnimations.STEP_RIGHT)
                        .setPerfectAnimations(
                                () -> CorruptAnimations.STEP_FORWARD,
                                () -> CorruptAnimations.STEP_BACKWARD,
                                () -> Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT,
                                () -> Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT).setCreativeTab(CDAddonItems.CREATIVE_MODE_TAB),
                CDmoveset.MOD_ID, "wolf_dodge");
    }

    @SubscribeEvent
    public static void BuildSkills(SkillBuildEvent onBuild) {
        WOLF_DODGE = onBuild.build(CDmoveset.MOD_ID, "wolf_dodge");
        YAMATO_STEP = onBuild.build(CDmoveset.MOD_ID, "yamato_step");
        SSTEP = onBuild.build(CDmoveset.MOD_ID, "sstep");
        WeaponInnateSkill swordslash = onBuild.build(CDmoveset.MOD_ID, "sword_slash");
        swordslash.newProperty()
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(3))
                .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        SWORD_SLASH = swordslash;

        WeaponInnateSkill dual_slash = onBuild.build(CDmoveset.MOD_ID, "dual_slash");
        dual_slash.newProperty()
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(3))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        DUAL_SLASH = dual_slash;


        WeaponInnateSkill spearslash = onBuild.build(CDmoveset.MOD_ID, "spearslash");
        spearslash.newProperty()
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(4))
                .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        SPEAR_SLASH = spearslash;

        WeaponInnateSkill LethalSlicingSkill = onBuild.build(CDmoveset.MOD_ID, "lethalslicing");
        LethalSlicingSkill.newProperty()
                .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(5.0F))
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(6))
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(0.0F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .newProperty()
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(6))
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(50.0F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        LETHAL_SLICING = LethalSlicingSkill;

        YAMATOSKILL  = onBuild.build(CDmoveset.MOD_ID, "yamatoskill");
        GUARDPARRY = onBuild.build(CDmoveset.MOD_ID, "guardparry");
        GREAT_TACHISKILL = onBuild.build(CDmoveset.MOD_ID, "greeat_tachiskill");
        SHILEDSLASH = onBuild.build(CDmoveset.MOD_ID, "shiled_slahs");



        WeaponInnateSkill wind_slash = onBuild.build(CDmoveset.MOD_ID, "wind_slash");
        wind_slash .newProperty()
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        WIND_SLASH = wind_slash;



        WeaponInnateSkill fatal_draw = onBuild.build(CDmoveset.MOD_ID, "fatal_draw");
        fatal_draw .newProperty()
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        FATAL_DRAW_DASH = fatal_draw;


        WeaponInnateSkill blade_rush_finisher = onBuild.build(CDmoveset.MOD_ID, "blade_rush_finisher");
        blade_rush_finisher .newProperty()
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        BLADE_RUSH_FINISHER = blade_rush_finisher;

        WeaponInnateSkill KatanaspSkill = onBuild.build(CDmoveset.MOD_ID, "katanaspskill");
        KatanaspSkill .newProperty()
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(15.0F))
                .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1F))
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(3))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2F))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        KATANASPSKILL = KatanaspSkill;

        WeaponInnateSkill KatanaSkill = onBuild.build(CDmoveset.MOD_ID, "katanaskill");
        KatanaSkill .newProperty()
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(15.0F))
                .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(3))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3F))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        KATANASKILL = KatanaSkill;


        WeaponInnateSkill greatsword_dual_skill = onBuild.build(CDmoveset.MOD_ID, "greatsword_dual_skill");
        greatsword_dual_skill .newProperty()
                .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
                .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE))
                .registerPropertiesToAnimation();
        DUAL_GREATSWORD_SKILL = greatsword_dual_skill;

    }
}