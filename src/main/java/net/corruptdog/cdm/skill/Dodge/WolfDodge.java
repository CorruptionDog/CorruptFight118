package net.corruptdog.cdm.skill.Dodge;

import io.netty.buffer.Unpooled;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.corruptdog.cdm.gameasset.CorruptSound;
import net.corruptdog.cdm.network.server.NetworkManager;
import net.corruptdog.cdm.network.server.SPAfterImagine;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;


public class WolfDodge extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("e33b7c1a-8909-47c1-9b95-81f9d86c412e");
    private static final SkillDataManager.SkillDataKey<Integer> COUNT = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//闪避计数器
    private static final SkillDataManager.SkillDataKey<Integer> DIRECTION = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//方向
    private static final SkillDataManager.SkillDataKey<Integer> RESET_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//回归第一段的时间
    public static final SkillDataManager.SkillDataKey<Boolean> DODGE_PLAYED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否播过完美闪避动画，防止重复播放
    public static final int RESET_TICKS = 100;
    protected final StaticAnimationProvider[][] animations;

    public static WolfDodge.Builder createDodgeBuilder() {
        return (new WolfDodge.Builder()).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
    }

    public WolfDodge(WolfDodge.Builder builder) {
        super(builder);
        animations = builder.animations;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COUNT);
        container.getDataManager().registerData(DIRECTION);
        container.getDataManager().registerData(RESET_TIMER);
        container.getDataManager().registerData(DODGE_PLAYED);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {

            Player player = event.getPlayerPatch().getOriginal();
            if(!container.getDataManager().getDataValue(DODGE_PLAYED)){
                if(player.level instanceof ServerLevel){
                    float consumption = container.getExecuter().getModifiedStaminaConsume(container.getExecuter().getSkill(SkillSlots.DODGE).getSkill().getConsumption());
                    container.getExecuter().setStamina(container.getExecuter().getStamina() + consumption * 0.5F);
                    ServerPlayer serverPlayer = (ServerPlayer)container.getExecuter().getOriginal();
                    SPAfterImagine msg = new SPAfterImagine(serverPlayer.position(), serverPlayer.getId());
                    NetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, serverPlayer);
                    event.getPlayerPatch().playSound(CorruptSound.DODGE, 1F, 1.2F);

                    Skill skill = container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill();
                    if (skill != null) {
                        SkillContainer weaponInnateContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                        weaponInnateContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), weaponInnateContainer.getResource() + 5F);
                    } else {
                        event.getPlayerPatch().playAnimationSynchronized(CorruptAnimations.WOLFDODGE_BACKWARD, 0.25F);
                    }
                }
                container.getDataManager().setData(DODGE_PLAYED, true);
                event.getPlayerPatch().playAnimationSynchronized(this.animations[3][container.getDataManager().getDataValue(DIRECTION)].get(), 0.0F);
            }
        }));
    }


    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
    }

    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        Input input = executer.getOriginal().input;
        input.tick(false);
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(forward);
        buf.writeInt(backward);
        buf.writeInt(left);
        buf.writeInt(right);
        return buf;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
        int forward = args.readInt();
        int backward = args.readInt();
        int left = args.readInt();
        int right = args.readInt();
        int vertic = forward + backward;
        int horizon = left + right;
        int degree = vertic == 0 ? 0 : -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
        int animation;

        if (vertic == 0) {
            if (horizon == 0) {
                animation = 0;
            } else {
                animation = horizon >= 0 ? 2 : 3;
            }
        } else {
            animation = vertic >= 0 ? 0 : 1;
        }

        CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
        packet.getBuffer().writeInt(animation);
        packet.getBuffer().writeFloat(degree);

        return packet;
    }
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
        return list;
    }

    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        int i = args.readInt();
        float yaw = args.readFloat();
        SkillDataManager dataManager = executer.getSkill(SkillSlots.DODGE).getDataManager();
        dataManager.setData(DODGE_PLAYED, false);
        int count = dataManager.getDataValue(COUNT);
//        executer.playAnimationSynchronized(this.animations[0][i].get(), 0.0F);
        executer.playAnimationSynchronized(this.animations[count][i].get(), 0.0F);//轮播
        executer.playSound(EpicFightSounds.ROLL, 1.0F, 1.0F);
        dataManager.setDataSync(DIRECTION, i, executer.getOriginal());//完美闪避用
        if(count != 0){
            dataManager.setDataSync(RESET_TIMER, RESET_TICKS, executer.getOriginal());
        }
        dataManager.setDataSync(COUNT, ++count % 3, executer.getOriginal());
        executer.changeModelYRot(yaw);
    }

    /**
     * 太久则复原第一段
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if(manager.hasData(RESET_TIMER) && manager.getDataValue(RESET_TIMER) > 0){
            manager.setData(RESET_TIMER, manager.getDataValue(RESET_TIMER) - 1);
            if(manager.getDataValue(RESET_TIMER) == 1 && manager.hasData(COUNT)){
                manager.setData(COUNT, 0);
            }
        }
    }

    public boolean isExecutableState(PlayerPatch<?> executer) {
        EntityState playerState = executer.getEntityState();
        return !executer.isUnstable() && playerState.canUseSkill() && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && executer.getOriginal().getVehicle() == null;
    }

    public static class Builder extends Skill.Builder<WolfDodge> {
        protected StaticAnimationProvider[][] animations = new StaticAnimationProvider[4][4];

        public Builder() {
        }

        public WolfDodge.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public WolfDodge.Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public WolfDodge.Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        public WolfDodge.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public WolfDodge.Builder setAnimations1(StaticAnimationProvider... animations) {
            this.animations[0] = animations;
            return this;
        }

        public WolfDodge.Builder setAnimations2(StaticAnimationProvider... animations) {
            this.animations[1] = animations;
            return this;
        }

        public WolfDodge.Builder setAnimations3(StaticAnimationProvider... animations) {
            this.animations[2] = animations;
            return this;
        }
        public WolfDodge.Builder setPerfectAnimations(StaticAnimationProvider... animations) {
            this.animations[3] = animations;
            return this;
        }
    }
}
