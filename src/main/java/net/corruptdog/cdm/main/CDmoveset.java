package net.corruptdog.cdm.main;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.corruptdog.cdm.CDConfig;
import net.corruptdog.cdm.gameasset.CDSkills;
import net.corruptdog.cdm.gameasset.CorruptAnimations;
import net.corruptdog.cdm.network.server.NetworkManager;
import net.corruptdog.cdm.world.CDWeaponCapabilityPresets;
import net.corruptdog.cdm.world.CorruptWeaponCategories;
import net.corruptdog.cdm.world.Render.A_YamatoRender;
import net.corruptdog.cdm.world.Render.KtanaSheathRenderer;
import net.corruptdog.cdm.world.Render.YamatoRender;
import net.corruptdog.cdm.world.item.CDAddonItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.resource.PathResourcePack;
import org.slf4j.Logger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;



@Mod(CDmoveset.MOD_ID)
public class CDmoveset
{
    public static final String MOD_ID = "cdmoveset";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static float PERCENT = 20F;
    public static double millisF = 0F;
    public static long millis = 0;
    public static Timer timer;
    public static ScheduledExecutorService service;

    public CDmoveset() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        WeaponCategory.ENUM_MANAGER.loadPreemptive(CorruptWeaponCategories.class);
        CDAddonItems.ITEMS.register(bus);
        bus.addListener(CDWeaponCapabilityPresets::register);
        bus.addListener(CorruptAnimations::registerAnimations);
        bus.addListener(this::doCommonStuff);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {bus.addListener(CDmoveset::registerRenderer);});
        CDSkills.registerSkills();
        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(CDSkills::BuildSkills);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CDConfig.SPEC);
        bus.addListener(CDmoveset::addPackFindersEvent);
        create();
    }
    @OnlyIn(Dist.CLIENT)
    public static void registerRenderer(PatchedRenderersEvent.Add event){
        event.addItemRenderer(CDAddonItems.KATANA.get(), new KtanaSheathRenderer());
        event.addItemRenderer(CDAddonItems.YAMATO.get(), new YamatoRender());
        event.addItemRenderer(CDAddonItems.A_YAMATO.get(), new A_YamatoRender());
    }

    @SubscribeEvent
    public static void addPackFindersEvent(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path resourcePath = ModList.get().getModFileById(CDmoveset.MOD_ID).getFile().findResource("packs/corrupt_animation");
            if (resourcePath != null && Files.exists(resourcePath)) {
                PathResourcePack pack = new PathResourcePack("corrupt_animation", resourcePath);
                Pack packInfo = Pack.create("corrupt_animation", false,
                        () -> pack, (id, displayName, alwaysEnabled, packResourcesSupplier, metadata, position, packSource, hidden)
                                -> new Pack(id, alwaysEnabled, packResourcesSupplier, displayName, metadata.getDescription(), PackCompatibility.forMetadata(metadata, event.getPackType()), Pack.Position.TOP, false, packSource, hidden), Pack.Position.TOP, PackSource.BUILT_IN);
                if (packInfo != null) {
                    event.addRepositorySource((repository, enabled) -> repository.accept(packInfo));
                }
            }

            resourcePath = ModList.get().getModFileById(CDmoveset.MOD_ID).getFile().findResource("packs/power");
            if (resourcePath != null && Files.exists(resourcePath)) {
                PathResourcePack pack = new PathResourcePack("More_Power By zhong004", resourcePath);
                Pack packInfo = Pack.create("More_Power", false, () -> pack,
                        (id, displayName, alwaysEnabled, packResourcesSupplier, metadata, position, packSource, hidden) ->
                                new Pack(id, alwaysEnabled, packResourcesSupplier, displayName, metadata.getDescription(), PackCompatibility.forMetadata(metadata, event.getPackType()), Pack.Position.TOP, false, packSource, hidden),
                        Pack.Position.TOP, PackSource.BUILT_IN);
                if (packInfo != null) {
                    event.addRepositorySource((repository, enabled) -> repository.accept(packInfo));
                }
            }
        }
    }


    private void doCommonStuff(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkManager::registerPackets);

    }
    public static void create() {
        if (service == null)
            service = Executors.newSingleThreadScheduledExecutor();

        if (timer == null)
            timer = new Timer();
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    service.scheduleAtFixedRate(CDmoveset::update, 1L, 1L, TimeUnit.MILLISECONDS);
                }
            }, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void update() {
        float p = PERCENT / 20.0F;
        millisF = p + millisF;
        millis = (long) millisF;
    }

    public static void changeAll(float percent) {
        PERCENT = percent;
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }




    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
