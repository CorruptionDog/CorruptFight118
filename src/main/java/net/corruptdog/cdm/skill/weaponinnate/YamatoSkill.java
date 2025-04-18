package net.corruptdog.cdm.skill.weaponinnate;


import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.sun.jna.platform.win32.WinBase;
import io.netty.buffer.Unpooled;
import net.corruptdog.cdm.CDConfig;
import net.corruptdog.cdm.gameasset.CDSkills;
import net.corruptdog.cdm.main.CDmoveset;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import static yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType.*;

public class YamatoSkill extends WeaponInnateSkill {
    private static final UUID EVENT_UUID = UUID.fromString("f082557a-b2f9-11eb-8529-0242ac130003");
    private final Map<ResourceLocation, Supplier<AttackAnimation>> comboAnimation = Maps.newHashMap();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final SkillDataManager.SkillDataKey<Boolean> COUNTER_SUCCESS = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    protected static final SkillDataManager.SkillDataKey<Integer> DAMAGES = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    protected static final SkillDataManager.SkillDataKey<Boolean> POWER3 = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    protected static final SkillDataManager.SkillDataKey<Boolean> COUNTER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    protected static final SkillDataManager.SkillDataKey<Boolean> POWER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);


    public YamatoSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COUNTER_SUCCESS);
        container.getDataManager().registerData(DAMAGES);
        container.getDataManager().registerData(POWER3);
        container.getDataManager().registerData(COUNTER);
        container.getDataManager().registerData(POWER);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.addEventListener(ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            StaticAnimation animation = event.getAnimation();
            ItemStack item = container.getExecuter().getOriginal().getMainHandItem();
            if (!container.getExecuter().isLogicalClient()) {
                item.getOrCreateTag().putBoolean("unsheathed", false);
            }
            if (!container.getExecuter().isLogicalClient()) {
                if (animation ==CorruptAnimations.YAMATO_AUTO3  || animation ==CorruptAnimations.YAMATO_AUTO4 || animation ==CorruptAnimations.YAMATO_POWER2
                        || animation ==CorruptAnimations.EX_YAMATO_AUTO4) {
                    item.getOrCreateTag().putBoolean("unsheathed", true);
                }
            }
            if (container.getExecuter().getSkill(SkillSlots.DODGE).isActivated()) {
                if (container.getExecuter() instanceof ServerPlayerPatch patch) {
                    BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, patch, patch.getSkill(SkillSlots.BASIC_ATTACK), null, 0);
                }
            }
            if (animation == CorruptAnimations.YAMATO_COUNTER2) {
                container.getDataManager().setDataSync(COUNTER_SUCCESS, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
            }
            if (animation == CorruptAnimations.YAMATO_STRIKE1 || animation == CorruptAnimations.YAMATO_STRIKE2) {
                container.getDataManager().setDataSync(COUNTER_SUCCESS, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
                container.getDataManager().setDataSync(COUNTER, false, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
            }
            if (!(animation == CorruptAnimations.YAMATO_POWER3 || animation == CorruptAnimations.YAMATO_POWER3_REPEAT)) {
                if (container.getDataManager().getDataValue(POWER3)) {
                    container.getDataManager().setData(POWER3, false);
                }
            }
            if (animation == CorruptAnimations.YAMATO_STRIKE1 || animation == CorruptAnimations.YAMATO_RISING_STAR ||  animation == CorruptAnimations.YAMATO_DAWN) {
                Skill skill = container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill();
                int strike1_cost = 1;
                int skillstack = event.getPlayerPatch().getSkill(CDSkills.YAMATOSKILL).getStack();
                if (skillstack >= 1) {
                    if (skill != null) {
                        this.stackCost(event.getPlayerPatch(), strike1_cost);
                    }
                }
            }
            if (!(animation == CorruptAnimations.YAMATO_POWER3
                    || animation == CorruptAnimations.YAMATO_POWER3_REPEAT
                    || animation == CorruptAnimations.YAMATO_POWER3_FINISH
                    || animation == CorruptAnimations.YAMATO_POWER_DASH
                    || animation == CorruptAnimations.YAMATO_POWER0_1)) {
                if (container.getDataManager().getDataValue(DAMAGES)> 1) {
                    container.getDataManager().setData(DAMAGES, 0);
                }
            }
        });
        listener.addEventListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
            int id = event.getAnimation().getId();
            if (id == CorruptAnimations.YAMATO_POWER3.getId() || id == CorruptAnimations.YAMATO_POWER3_REPEAT.getId()) {
                event.getPlayerPatch().reserveAnimation(CorruptAnimations.YAMATO_POWER3_FINISH);
            } else if (id == CorruptAnimations.YAMATO_COUNTER1.getId()) {
                container.getDataManager().setDataSync(COUNTER_SUCCESS, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
                event.getPlayerPatch().reserveAnimation(CorruptAnimations.YAMATO_COUNTER2);
            } else if (id == CorruptAnimations.YAMATO_POWER3_FINISH.getId() || id == CorruptAnimations.YAMATO_POWER_DASH.getId()) {
                container.getDataManager().setData(DAMAGES, 0);
            }  else if (id == CorruptAnimations.YAMATO_DAWN.getId()) {
                event.getPlayerPatch().reserveAnimation(CorruptAnimations.YAMATO_DAWN_END);
            } else if (id == CorruptAnimations.YAMATO_DAWN_END.getId()) {
                int end_recover = 1;
                ServerPlayerPatch playerPatch = (ServerPlayerPatch) container.getExecuter();
                if (playerPatch != null) {
                    this.stackCost(playerPatch, -end_recover);
                }
            }
        });
        listener.addEventListener(BASIC_ATTACK_EVENT, EVENT_UUID, (event) -> {
            float stamina = event.getPlayerPatch().getStamina();
            ResourceLocation rl = event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation().getRegistryName();
            if (stamina < 3) {
                if (rl == CorruptAnimations.YAMATO_POWER3.getRegistryName() || rl == CorruptAnimations.YAMATO_POWER3_REPEAT.getRegistryName()) {
                    event.setCanceled(true);
                }
            }
            ServerPlayerPatch executer = event.getPlayerPatch();
            if (rl == CorruptAnimations.YAMATO_RISING_STAR.getRegistryName()
                    && !executer.getOriginal().isOnGround()
                    && !executer.getOriginal().isInWater()
                    && !executer.getOriginal().onClimbable()) {
                event.setCanceled(true);
                executer.playAnimationSynchronized(CorruptAnimations.YAMATO_DAWN, 0);
            }
        });

        listener.addEventListener(DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
            int id = event.getDamageSource().getAnimation().getId();
            float maxstamina = event.getPlayerPatch().getMaxStamina();
            float stamina = event.getPlayerPatch().getStamina();
            if (id == CorruptAnimations.YAMATO_POWER1.getId()) {
                float r = 0.25F;
                if (stamina < maxstamina) {
                    container.getExecuter().setStamina(stamina + r * maxstamina);
                }
            } else if (id == CorruptAnimations.YAMATO_COUNTER2.getId()) {
                float c = 0.1F;
                if (stamina < maxstamina) {
                    container.getExecuter().setStamina(stamina + c * maxstamina);
                }
            }
            if (id == CorruptAnimations.YAMATO_POWER3_REPEAT.getId() || id == CorruptAnimations.YAMATO_POWER3.getId()) {
                Integer k = container.getDataManager().getDataValue(DAMAGES);
                container.getDataManager().setData(DAMAGES, k + 1);
            }
            if (event.getDamageSource() != null) {
                float attackDamage = event.getAttackDamage();
                float bonus = 0.23F;
                int max = 35;
                if (id == CorruptAnimations.YAMATO_POWER3_FINISH.getId() || id == CorruptAnimations.YAMATO_POWER_DASH.getId()) {
                    Integer k = container.getDataManager().getDataValue(DAMAGES);
                    k = Math.min(k, max);
                    event.setAttackDamage(attackDamage * (1F + bonus * k));
                }
            }
        });
        listener.addEventListener(PlayerEventListener.EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
            ResourceLocation rl = event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation().getRegistryName();
            Integer K = container.getDataManager().getDataValue(DAMAGES);
            int max = 25;
            if (rl == CorruptAnimations.YAMATO_POWER3_REPEAT.getRegistryName() || rl == CorruptAnimations.YAMATO_POWER3.getRegistryName()) {
                container.getDataManager().setData(DAMAGES, K + 1);
            }

            float bonus = 0.15F;
            if (rl == CorruptAnimations.YAMATO_POWER3_FINISH.getRegistryName() || rl == CorruptAnimations.YAMATO_POWER_DASH.getRegistryName()) {
                K = Math.min(K, max);
                event.setDamage(event.getDamage() * (1.0F + bonus * (float)K));
            }
        });
        listener.addEventListener(SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            container.getExecuter().getEventListener().addEventListener(SKILL_EXECUTE_EVENT, EVENT_UUID, (power) -> {
                ServerPlayerPatch executer = event.getPlayerPatch();
                int skillstack = event.getPlayerPatch().getSkill(CDSkills.YAMATOSKILL).getStack();
                int power_cost = 5;
                if (skillstack == 5 && executer.getOriginal().isCrouching() && executer.getSkill(SkillSlots.WEAPON_INNATE).hasSkill(CDSkills.YAMATOSKILL)) {
                    power.setCanceled(true);
                    executer.playAnimationSynchronized(CorruptAnimations.YAMATO_JUDEGMENT_CUT_END, 0);
                    this.stackCost(event.getPlayerPatch(), power_cost);
                }
            });
        });

        listener.addEventListener(HURT_EVENT_PRE, EVENT_UUID, (event) -> {
            int power2_recover = 2;
            Skill skill = container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill();
            ServerPlayerPatch executer = event.getPlayerPatch();
            AnimationPlayer animationPlayer = executer.getAnimator().getPlayerFor(null);
            float elapsedTime = animationPlayer.getElapsedTime();
            int animationId = executer.getAnimator().getPlayerFor(null).getAnimation().getId();
            if (elapsedTime <= 0.40F) {
                if (animationId == CorruptAnimations.YAMATO_POWER0_1.getId()) {
                    DamageSource damagesource = event.getDamageSource();
                    Vec3 sourceLocation = damagesource.getSourcePosition();
                    if (sourceLocation != null) {
                        Vec3 viewVector = event.getPlayerPatch().getOriginal().getViewVector(1.0F);
                        Vec3 toSourceLocation = sourceLocation.subtract(event.getPlayerPatch().getOriginal().position()).normalize();
                        if (toSourceLocation.dot(viewVector) > 0.0D) {
                            if (damagesource instanceof EntityDamageSource
                                    && !damagesource.isExplosion()
                                    && !damagesource.isMagic()
                                    && !damagesource.isBypassArmor()
                                    && !damagesource.isBypassInvul()) {
                                POWER0_2(executer);
                                container.getDataManager().setDataSync(COUNTER_SUCCESS, true, ((ServerPlayerPatch) container.getExecuter()).getOriginal());
                                scheduler.schedule(() -> executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(COUNTER_SUCCESS, false), 1500, TimeUnit.MILLISECONDS);
                                if (skill != null) {
                                    this.stackCost(executer, -power2_recover);
                                } else {
                                    event.getPlayerPatch().playAnimationSynchronized(CorruptAnimations.YAMATO_POWER0_2, 0.15F);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    @Override
    public void onReset(SkillContainer container) {
        PlayerPatch<?> executer = container.getExecuter();
        ItemStack item = executer.getOriginal().getMainHandItem();

        if (!executer.isLogicalClient()) {
            item.getOrCreateTag().putBoolean("unsheathed",false);
        }
    }

    public void onRemoved(SkillContainer container) {
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
    }

    private void STRIKE2(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_STRIKE2, 0F);
    }
    private void POWER_1(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER1, 0.0F);
    }
    private void POWER_2(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER2, 0.0F);
    }
    private void POWER_3(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER3, 0.15F);
    }
    private void POWER_DASH(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER_DASH, 0.25F);
    }
    private void POWER_DASHS(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER_DASH, -0.3F);
    }
    private void POWER0_1(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER0_1, 0.0F);
    }
    private void POWER0_2(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_POWER0_2, 0.05F);
    }
    private void COUNTER(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_COUNTER1, 0.25F);
            if (CDConfig.SLOW_TIME.get() && executer.getOriginal().level.getServer() != null && !FMLEnvironment.dist.isDedicatedServer() &&
                    Objects.requireNonNull(executer.getOriginal().level.getServer()).getPlayerCount() <= 1) {
                CDmoveset.changeAll(10);
                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                scheduledExecutorService.schedule(() -> {
                    this.Slow_time(executer);
                }, 1500L, TimeUnit.MILLISECONDS);
        }
    }
    private void RISING(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_RISING_SLASH, 0.25F);
    }
    private void RISING_STAR(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_RISING_STAR, 0.25F);
    }
    private void DAWN(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_DAWN, 0.25F);
    }
    private void POWER(ServerPlayerPatch executer) {
        executer.playAnimationSynchronized(CorruptAnimations.YAMATO_JUDEGMENT_CUT_END, 0.0F);
    }
    @Unique
    public void Slow_time(ServerPlayerPatch container) {
        Level var3 = container.getOriginal().level;
        if (container.getOriginal().level.getServer() != null && !FMLEnvironment.dist.isDedicatedServer() && container.getOriginal().level.getServer().getPlayerCount() <= 1) {
            if (var3 instanceof ServerLevel) {
                CDmoveset.changeAll(20);
            }
        }
    }
    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        ResourceLocation rl = executer.getAnimator().getPlayerFor(null).getAnimation().getRegistryName();
        if (rl == CorruptAnimations.YAMATO_POWER3.getRegistryName() || rl == CorruptAnimations.YAMATO_POWER3_REPEAT.getRegistryName()
                || rl == CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT.getRegistryName() || rl == CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT2.getRegistryName()
                || rl == CorruptAnimations.YAMATO_COUNTER1.getRegistryName() || rl == CorruptAnimations.YAMATO_POWER0_2.getRegistryName()
                || rl == CorruptAnimations.YAMATO_TWIN_SLASH.getRegistryName() || rl == CorruptAnimations.YAMATO_RISING_STAR.getRegistryName()  || rl == CorruptAnimations.YAMATO_POWER1.getRegistryName()) {
            return true;
        } else if (executer.isLogicalClient()) {
            return executer.getEntityState().canBasicAttack();
        } else {
            ItemStack itemstack = executer.getOriginal().getMainHandItem();
            return executer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getInnateSkill(executer, itemstack) == this;
        }
    }
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return this;
    }


    private void stackCost(ServerPlayerPatch player, int cost) {
        if ( player.getSkill(SkillSlots.WEAPON_INNATE).hasSkill(CDSkills.YAMATOSKILL)) {
            this.setStackSynchronize(player, player.getSkill(CDSkills.YAMATOSKILL).getStack() - cost);
        }
    }
    @Override
    public void executeOnServer(ServerPlayerPatch execute, FriendlyByteBuf args) {
        ResourceLocation rl = execute.getAnimator().getPlayerFor(null).getAnimation().getRegistryName();
        SkillContainer skillContainer = execute.getSkill(SkillSlots.WEAPON_INNATE);
        Boolean counterSuccess = skillContainer.getDataManager().getDataValue(COUNTER_SUCCESS);
        Boolean counter = skillContainer.getDataManager().getDataValue(COUNTER);
        Boolean power3 = skillContainer.getDataManager().getDataValue(POWER3);
        if (power3) {
            POWER_DASHS(execute);
        }
        if (counterSuccess) {
            STRIKE2(execute);
            return;
        }
        if (counter) {
            STRIKE2(execute);
            return;
        }
        if (execute.getOriginal().isSprinting() && execute.getSkill(CDSkills.YAMATOSKILL).getStack() > 0) {
            float stamina = execute.getStamina();
            float maxStamina = execute.getMaxStamina();
            float p = maxStamina * 0.25F;
            if (stamina >= p) {
                POWER_DASH(execute);
                execute.setStamina(stamina - p);
            }
        } else {
            if (this.comboAnimation.containsKey(rl)) {
                execute.playAnimationSynchronized(this.comboAnimation.get(rl).get(), 0.0F);
            } else {
                    if (canExecute(execute)) {
                        POWER0_1(execute);
                }else {
                    return;
                }
                Map<ResourceLocation, Runnable> actionMap = Maps.newHashMap();
                actionMap.put(CorruptAnimations.YAMATO_AUTO1.getRegistryName(), () -> POWER_1(execute));
                actionMap.put(CorruptAnimations.YAMATO_AUTO2.getRegistryName(), () -> POWER_2(execute));
                actionMap.put(CorruptAnimations.YAMATO_AUTO3.getRegistryName(), () -> POWER_3(execute));
                actionMap.put(CorruptAnimations.YAMATO_POWER1.getRegistryName(), () -> RISING_STAR(execute));
                actionMap.put(CorruptAnimations.YAMATO_RISING_STAR.getRegistryName(), () -> DAWN(execute));
                actionMap.put(CorruptAnimations.YAMATO_TWIN_SLASH.getRegistryName(), () -> RISING(execute));
                actionMap.put(CorruptAnimations.YAMATO_TURN_SLASH.getRegistryName(), () -> POWER_DASHS(execute));
                actionMap.put(CorruptAnimations.YAMATO_POWER3.getRegistryName(), () -> POWER_DASHS(execute));
                actionMap.put(CorruptAnimations.YAMATO_POWER3_REPEAT.getRegistryName(), () -> POWER_DASHS(execute));
                actionMap.put(CorruptAnimations.YAMATO_STRIKE1.getRegistryName(), () -> POWER_2(execute));
                actionMap.put(CorruptAnimations.YAMATO_STRIKE2.getRegistryName(), () -> POWER_3(execute));
                actionMap.put(CorruptAnimations.YAMATO_COUNTER1.getRegistryName(), () -> STRIKE2(execute));
                actionMap.put(CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT.getRegistryName(), () -> COUNTER(execute));
                actionMap.put(CorruptAnimations.YAMATO_ACTIVE_GUARD_HIT2.getRegistryName(), () -> COUNTER(execute));
                if (actionMap.containsKey(rl)) {
                    actionMap.get(rl).run();
                }
                super.executeOnServer(execute, args);
            }
        }
    }
}
