package net.corruptdog.cdm.mixins;

import org.spongepowered.asm.mixin.*;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.skill.BattojutsuPassive;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

import static yesman.epicfight.skill.BattojutsuPassive.SHEATH;

@Mixin(value = BattojutsuPassive.class,remap = false)
public class BattojutsuPassiveMixin extends Skill {
    public BattojutsuPassiveMixin(Builder<? extends Skill> builder) {
        super(builder);
    }
    @Mutable
    @Final
    @Shadow
    private static final UUID EVENT_UUID;
    static {
        EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(SHEATH);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            if (!(event.getAnimation() instanceof DodgeAnimation)) {
                container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), 0.0F);
                container.getSkill().setStackSynchronize(event.getPlayerPatch(), 0);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            this.onReset(container);
        });
    }
}
