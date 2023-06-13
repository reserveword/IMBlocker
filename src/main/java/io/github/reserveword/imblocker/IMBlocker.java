package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlocker.MODID)
public class IMBlocker {
    public static final String MODID = "imblocker";
    // Directly reference a log4j logger.
    static final Logger LOGGER = LogManager.getLogger();

    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoadReload);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            IMCheckState.clientTick(cte);
        }
        @SubscribeEvent
        public static void onMouseClick(ScreenEvent.MouseButtonPressed mie) {
            IMCheckState.mouseEvent(mie);
        }
        @SubscribeEvent
        public static void onMouseClick(ScreenEvent.MouseButtonReleased mie) {
            IMCheckState.mouseEvent(mie);
        }
    }

    @SubscribeEvent
    public void onConfigLoadReload(ModConfigEvent e) {
        LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
        Config.reload();
    }
}
