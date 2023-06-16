package io.github.reserveword.imblocker;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoadReload);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            if (cte.phase == TickEvent.Phase.START) {
                IMCheckState.clientTick(new ForgeScreenInfo());
            }
        }
        @SubscribeEvent
        public static void onMouseClick(ScreenEvent.MouseButtonPressed mie) {
            IMCheckState.mouseEvent();
        }
        @SubscribeEvent
        public static void onMouseClick(ScreenEvent.MouseButtonReleased mie) {
            IMCheckState.mouseEvent();
        }
    }

    @SubscribeEvent
    public void onConfigLoadReload(ModConfigEvent e) {
        Common.LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
        ForgeConfig.reload();
    }
}
