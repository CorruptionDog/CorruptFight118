package net.corruptdog.cdm.mixins;

import net.corruptdog.cdm.CDConfig;
import net.corruptdog.cdm.gameasset.CorruptSound;
import net.corruptdog.cdm.main.CDmoveset;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(
        value = {DodgeSkill.class},
        remap = false
)
public class DodgeSkillMixin extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("99e5c782-fdaf-11eb-9a03-0242ac130005");

    public DodgeSkillMixin(Skill.Builder<? extends Skill> builder) {
        super(builder);
    }

    @Unique
    public void Slow_time(SkillContainer container) {
        Level var3 = container.getExecuter().getOriginal().getLevel();
        if (container.getExecuter().getOriginal().getLevel().getServer() != null && !FMLEnvironment.dist.isDedicatedServer() && container.getExecuter().getOriginal().getLevel().getServer().getPlayerCount() <= 1) {
            if (var3 instanceof ServerLevel _level) {
                _level.getServer().getCommands().performCommand((new CommandSourceStack(CommandSource.NULL, new Vec3(container.getExecuter().getOriginal().getX(), ((Player)container.getExecuter().getOriginal()).getY(), container.getExecuter().getOriginal().getZ()), Vec2.ZERO, _level, 4, "", new TextComponent(""), _level.getServer(), null)).withSuppressedOutput(), "tickrate change 20");
            }
        }
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
            if (CDConfig.ENABLE_DODGESUCCESS_SOUND.get() ) {
                container.getExecuter().playSound(CorruptSound.FORESIGHT, 0.8F, 1.2F);
            }
            if (CDConfig.SLOW_TIME.get() && container.getExecuter().getOriginal().level.getServer() != null && !FMLEnvironment.dist.isDedicatedServer() && Objects.requireNonNull(container.getExecuter().getOriginal().level.getServer()).getPlayerCount() <= 1) {

                CDmoveset.changeAll(8);

                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                scheduledExecutorService.schedule(() -> {
                    this.Slow_time(container);
                }, 500L, TimeUnit.MILLISECONDS);
            }
        });

        container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            StaticAnimation animation = event.getAnimation();
            if (CDConfig.ENABLE_DODGE_SOUND.get()) {
                if (animation == Animations.BIPED_STEP_FORWARD || animation == Animations.BIPED_STEP_BACKWARD || animation == Animations.BIPED_STEP_LEFT || animation == Animations.BIPED_STEP_RIGHT) {
                    event.getPlayerPatch().playSound(CorruptSound.STEP, 0.8F, 1.0F);
                }
            }
        });
    }

    @Unique
    public void onRemoved(SkillContainer container) {
        container.getExecuter().getEventListener().removeListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
    }
}