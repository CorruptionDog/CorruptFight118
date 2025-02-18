package net.corruptdog.cdm.gameasset;

import net.corruptdog.cdm.main.CDmoveset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;

public class CorruptSound {
    public static final SoundEvent YAMATO_STEP = registerSound("sfx.yamato_step");
    public static final SoundEvent YAMATO_IN = registerSound("yamato.yamato_in");
    public static final SoundEvent YAMATO_CUT1 = registerSound("yamato.cut1");
    public static final SoundEvent YAMATO_CUT2 = registerSound("yamato.cut2");
    public static final SoundEvent FORESIGHT = registerSound("sfx.foresight");
    public static final SoundEvent EXECUTE = registerSound("sfx.execute");
    public static final SoundEvent DAMAGE = registerSound("sfx.damage");
    public static final SoundEvent DODGE = registerSound("sfx.dodge");
    public static final SoundEvent SKILL = registerSound("sfx.skills");
    public static final SoundEvent HURT = registerSound("sfx.hurt");
    public static final SoundEvent STEP = registerSound("sfx.step");
    public static final SoundEvent FATALFLASH = registerSound("sfx.fatalflash");
    private static SoundEvent registerSound(String name) {
        ResourceLocation res = new ResourceLocation(CDmoveset.MOD_ID, name);
        SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
        return soundEvent;
    }

    public static void registerSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                YAMATO_STEP,
                YAMATO_IN,
                YAMATO_CUT1,
                YAMATO_CUT2,
                FORESIGHT,
                EXECUTE,
                DAMAGE,
                DODGE,
                SKILL,
                HURT,
                STEP,
                FATALFLASH
        );
    }
}