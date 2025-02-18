package net.corruptdog.cdm.skill.weaponinnate;

import net.corruptdog.cdm.gameasset.CDSkills;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

import java.util.UUID;

public class YamatoPassive extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("a446c93a-42cb-11eb-b378-0242ac130002");

    public YamatoPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        PlayerPatch<?> executer = container.getExecuter();
        ItemStack item = executer.getOriginal().getMainHandItem();
        SkillContainer step = executer.getSkillCapability().skillContainers[SkillCategories.DODGE.universalOrdinal()];
        if(!(step.isEmpty()) && step.hasSkill(CDSkills.YAMATO_STEP)){
            step.setSkill(CDSkills.EX_YAMATO_STEP);
        }
        if (!executer.isLogicalClient()) {
            item.getOrCreateTag().putBoolean("unsheathed",false);
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        PlayerPatch executer = container.getExecuter();
        SkillContainer exdodgeskill = executer.getSkillCapability().skillContainers[SkillCategories.DODGE.universalOrdinal()];
        executer.getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        if(exdodgeskill.hasSkill(CDSkills.EX_YAMATO_STEP)){
            exdodgeskill.setSkill(CDSkills.YAMATO_STEP);
        }
    }
}