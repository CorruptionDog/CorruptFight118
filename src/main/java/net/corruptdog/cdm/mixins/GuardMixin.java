package net.corruptdog.cdm.mixins;

import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.corruptdog.cdm.world.CorruptWeaponCategories;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Mixin(value = GuardSkill.class, remap = false)
public abstract class GuardMixin extends Skill {
    private static final SkillDataManager.SkillDataKey<Integer> PENALTY_RESTORE_COUNTER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);

    public GuardMixin(Builder<? extends Skill> builder) {
        super(builder);
    }
    @Shadow
    protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
        return !damageSource.isBypassInvul() && !damageSource.isBypassArmor() && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isMagic() && !damageSource.isFire();
    }

    @Shadow
    protected float getPenalizer(CapabilityItem itemCapability) {
        return this.penalizer;
    }

    @Shadow
    public abstract void dealEvent(PlayerPatch<?> playerpatch, HurtEvent.Pre event, boolean advanced);

    @Final
    @Shadow
    protected static SkillDataManager.SkillDataKey<Float> PENALTY;
    @Shadow
    @Final
    protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions;
    @Final
    @Shadow
    protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> advancedGuardMotions;
    @Final
    @Shadow
    protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardBreakMotions;
    @Shadow
    protected float penalizer;
    @Shadow
    @Nullable
    protected abstract StaticAnimation getGuardMotion(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, GuardSkill.BlockType blockType);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static GuardSkill.Builder createGuardBuilder() {
        return (new GuardSkill.Builder())
                .setCategory(SkillCategories.GUARD)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.STAMINA)
                .addGuardMotion(CapabilityItem.WeaponCategories.AXE, (item, player) -> Animations.SWORD_GUARD_HIT)
                .addGuardMotion(CapabilityItem.WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_HIT)
                .addGuardMotion(CapabilityItem.WeaponCategories.UCHIGATANA, (item, player) -> Animations.UCHIGATANA_GUARD_HIT)
                .addGuardMotion(CapabilityItem.WeaponCategories.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
                .addGuardMotion(CapabilityItem.WeaponCategories.SPEAR, (item, player) -> item.getStyle(player) == CapabilityItem.Styles.TWO_HAND ? Animations.SPEAR_GUARD_HIT : null)
                .addGuardMotion(CapabilityItem.WeaponCategories.SWORD, (item, player) -> item.getStyle(player) == CapabilityItem.Styles.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
                .addGuardMotion(CapabilityItem.WeaponCategories.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.AXE, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_BREAK)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.UCHIGATANA, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.LONGSWORD, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.SPEAR, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.SWORD, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardBreakMotion(CapabilityItem.WeaponCategories.TACHI, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                //
                // Corrupt Weapons
                .addGuardMotion(CorruptWeaponCategories.YAMATO, (item, player) -> CorruptAnimations.YAMATO_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.YAMATO, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.S_TACHI, (item, player) -> CorruptAnimations.TACHI_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.S_TACHI, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.S_LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.S_LONGSWORD, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.S_GREATSWORD, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.TWO_HAND ? Animations.LONGSWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.S_GREATSWORD, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.TWO_HAND ? Animations.GREATSWORD_GUARD_BREAK : Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.S_SPEAR, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.TWO_HAND ? Animations.SPEAR_GUARD_HIT : null)
                .addGuardBreakMotion(CorruptWeaponCategories.S_SPEAR, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.S_SWORD, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.S_SWORD, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.ONE_HAND ? Animations.GREATSWORD_GUARD_BREAK : Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.KATANA, (item, player) -> Animations.UCHIGATANA_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.KATANA, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
                .addGuardMotion(CorruptWeaponCategories.GREAT_TACHI, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.ONE_HAND ? CorruptAnimations.TACHI_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
                .addGuardBreakMotion(CorruptWeaponCategories.GREAT_TACHI, (item, player)
                        -> item.getStyle(player) == CapabilityItem.Styles.ONE_HAND ? Animations.GREATSWORD_GUARD_BREAK : Animations.BIPED_COMMON_NEUTRALIZED);

    }
    @Inject(method = "guard", remap = false, at = @At("HEAD"), cancellable = true)
    public void guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean advanced, CallbackInfo ci) {
        DamageSource damageSource = event.getDamageSource();

        if (this.isBlockableSource(damageSource, advanced)) {
            event.getPlayerPatch().playSound(EpicFightSounds.CLASH, -0.05F, 0.1F);
            ServerPlayer serveerPlayer = event.getPlayerPatch().getOriginal();
            EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serveerPlayer.getLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serveerPlayer, damageSource.getDirectEntity());

            if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity) {
                knockback += EnchantmentHelper.getKnockbackBonus(livingEntity) * 0.1F;
            }

            float penalty = container.getDataManager().getDataValue(PENALTY) + getPenalizer(itemCapability);
            float consumeAmount = penalty * impact;
            event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
            event.getPlayerPatch().consumeStaminaAlways(consumeAmount);
            container.getDataManager().setDataSync(PENALTY, penalty, event.getPlayerPatch().getOriginal());
            GuardSkill.BlockType blockType = event.getPlayerPatch().hasStamina(0.0F) ? GuardSkill.BlockType.GUARD : GuardSkill.BlockType.GUARD_BREAK;
            StaticAnimation animation = this.getGuardMotion(event.getPlayerPatch(), itemCapability, blockType);

            if (animation != null) {
                event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
            }

            if (blockType == GuardSkill.BlockType.GUARD_BREAK) {
                event.getPlayerPatch().playSound(EpicFightSounds.NEUTRALIZE_MOBS, 3.0F, 0.0F, 0.1F);
            }
            this.dealEvent(event.getPlayerPatch(), event, advanced);
        }
    }
}