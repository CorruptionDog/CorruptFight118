package net.corruptdog.cdm.mixins;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.weaponinnate.RushingTempoSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

import java.util.Map;

@Mixin(value = RushingTempoSkill.class,remap = false)
public class RushingTempoSkillMixin extends WeaponInnateSkill {
    public RushingTempoSkillMixin(Builder<? extends Skill> builder) {
        super(builder);
    }
    @Shadow
    private Map<ResourceLocation, AttackAnimation> comboAnimation = Maps.newHashMap();
    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        this.comboAnimation.values().forEach((animation) -> animation.phases[0].addProperties(((Map)this.properties.get(0)).entrySet()));
        return this;
    }


    @Inject(method = "<init>(Lyesman/epicfight/skill/Skill$Builder;)V", at = @At("RETURN"))
    public void init(Skill.Builder<? extends Skill> builder, CallbackInfo ci) {
        this.comboAnimation.put(Animations.TACHI_AUTO1.getRegistryName(), (AttackAnimation)Animations.RUSHING_TEMPO1);
        this.comboAnimation.put(Animations.TACHI_AUTO2.getRegistryName(), (AttackAnimation)Animations.RUSHING_TEMPO2);
        this.comboAnimation.put(Animations.TACHI_AUTO3.getRegistryName(), (AttackAnimation)Animations.RUSHING_TEMPO3);
        this.comboAnimation.put(Animations.TACHI_DASH.getRegistryName(), (AttackAnimation)Animations.RUSHING_TEMPO2);
        this.comboAnimation.put(Animations.LONGSWORD_AIR_SLASH.getRegistryName(), (AttackAnimation)Animations.RUSHING_TEMPO3);
    }
}

