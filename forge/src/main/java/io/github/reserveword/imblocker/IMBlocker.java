package io.github.reserveword.imblocker;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
    public IMBlocker(ModContainer container) {
        // Register ourselves for server and other game events we are interested in
        container.registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre cte) {
            IMCheckState.clientTick(new ForgeScreenInfo());
        }
        @SubscribeEvent
        public static void onMouseButtonReleased(ScreenEvent.MouseButtonReleased.Post mbr) {
            IMCheckState.scheduleTickCheck();
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onConfigLoadReload(ModConfigEvent e) {
            Common.LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
            ForgeConfig.reload();
        }
    }
}
