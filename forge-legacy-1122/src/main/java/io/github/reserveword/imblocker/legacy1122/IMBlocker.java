package io.github.reserveword.imblocker.legacy1122;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = IMBlocker.MOD_ID, name = IMBlocker.NAME, version = IMBlocker.VERSION,
        acceptedMinecraftVersions = "[1.12.2]", clientSideOnly = true)
public final class IMBlocker {
    public static final String MOD_ID = "imblocker";
    public static final String NAME = "IMBlocker";
    public static final String VERSION = "5.6.2.1-1.12.2";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[IMBlocker] Forge 1.12.2 client module loaded; Windows IMM32 backend will be initialized.");
        LegacyConfig.load(event.getSuggestedConfigurationFile());
        ClientRegistry.registerKeyBinding(LegacyConfig.unlockKey);
        MinecraftForge.EVENT_BUS.register(LegacyEventHandler.INSTANCE);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LegacyStateController.INSTANCE.initialize();
    }
}
