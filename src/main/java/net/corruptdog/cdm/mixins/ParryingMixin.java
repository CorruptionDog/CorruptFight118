package net.corruptdog.cdm.mixins;

import javax.annotation.Nullable;

import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.corruptdog.cdm.world.CorruptWeaponCategories;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import static yesman.epicfight.skill.guard.ParryingSkill.PARRY_MOTION_COUNTER;

@Mixin(
        value = {ParryingSkill.class},
        priority = 5000,
        remap = false
)
public abstract class ParryingMixin extends GuardSkill {

    @Shadow
    @Nullable
    protected abstract StaticAnimation getGuardMotion(PlayerPatch<?> var1, CapabilityItem var2, GuardSkill.BlockType var3);

    public ParryingMixin(GuardSkill.Builder builder) {
        super(builder);
    }
    @Mutable
    @Final
    @Shadow
    private static final SkillDataManager.SkillDataKey<Integer> LAST_ACTIVE;

    static {
        LAST_ACTIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public static GuardSkill.Builder createActiveGuardBuilder() {
        return GuardSkill.createGuardBuilder()
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.SWORD, (itemCap, playerpatch) -> itemCap.getStyle(playerpatch) == CapabilityItem.Styles.ONE_HAND ?
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2 } :
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT2, Animations.SWORD_GUARD_ACTIVE_HIT3 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.LONGSWORD, (itemCap, playerpatch) ->
                        new StaticAnimation[] { Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.UCHIGATANA, (itemCap, playerpatch) ->
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.TACHI, (itemCap, playerpatch) ->
                        new StaticAnimation[] { Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.SPEAR, (itemCap, playerpatch) ->
                        new StaticAnimation[] { Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.AXE, (itemCap, playerpatch) -> itemCap.getStyle(playerpatch) == CapabilityItem.Styles.ONE_HAND ?
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2 } :
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT2, Animations.SWORD_GUARD_ACTIVE_HIT3 })
                .addAdvancedGuardMotion(CapabilityItem.WeaponCategories.GREATSWORD, (itemCap, playerpatch) ->
                        new StaticAnimation[] { Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2 })
                //
                // Corrupt Weapons
                .addAdvancedGuardMotion(CorruptWeaponCategories.YAMATO, (itemCap, playerpatch)
                        -> new StaticAnimation[]{CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT, CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT2})
                .addAdvancedGuardMotion(CorruptWeaponCategories.S_LONGSWORD, (itemCap, playerpatch)
                        -> new StaticAnimation[]{Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2})
                .addAdvancedGuardMotion(CorruptWeaponCategories.S_SWORD, (itemCap, playerpatch)
                        -> itemCap.getStyle(playerpatch) == CapabilityItem.Styles.ONE_HAND ?
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2 } :
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT2, Animations.SWORD_GUARD_ACTIVE_HIT3 })
                .addAdvancedGuardMotion(CorruptWeaponCategories.S_GREATSWORD, (itemCap, playerpatch)
                        -> itemCap.getStyle(playerpatch) == CapabilityItem.Styles.TWO_HAND ?
                        new StaticAnimation[] { Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2 } :
                        new StaticAnimation[] { Animations.SWORD_GUARD_ACTIVE_HIT2, Animations.SWORD_GUARD_ACTIVE_HIT3 })

                .addAdvancedGuardMotion(CorruptWeaponCategories.S_TACHI, (itemCap, playerpatch)
                        -> new StaticAnimation[]{Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2})
                .addAdvancedGuardMotion(CorruptWeaponCategories.S_SPEAR, (itemCap, playerpatch)
                        -> new StaticAnimation[]{Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2})
                .addAdvancedGuardMotion(CorruptWeaponCategories.KATANA, (itemCap, playerpatch)
                        -> new StaticAnimation[]{Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2})
                .addAdvancedGuardMotion(CorruptWeaponCategories.GREAT_TACHI, (itemCap, playerpatch)
                        -> new StaticAnimation[]{Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2});

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(LAST_ACTIVE);
        container.getDataManager().registerData(PARRY_MOTION_COUNTER);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
            if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
                event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
            }

            int lastActive = container.getDataManager().getDataValue(LAST_ACTIVE);
            if ((float) (event.getPlayerPatch().getOriginal().tickCount - lastActive) > 0.1F) {
                container.getDataManager().setData(LAST_ACTIVE, event.getPlayerPatch().getOriginal().tickCount);
            }

        });
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"onInitiate"},
            cancellable = true
    )
    public void Parrying(SkillContainer container, CallbackInfo ci) {
        ci.cancel();
        super.onInitiate(container);
        container.getDataManager().registerData(LAST_ACTIVE);
        container.getDataManager().registerData(PARRY_MOTION_COUNTER);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
            if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
                event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
            }

            int lastActive = container.getDataManager().getDataValue(LAST_ACTIVE);
            if ((float) (event.getPlayerPatch().getOriginal().tickCount - lastActive) > 0.1F) {
                container.getDataManager().setData(LAST_ACTIVE, event.getPlayerPatch().getOriginal().tickCount);
            }

        });
    }
}
