package io.github.reserveword.imblocker;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.TickEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            if (cte.phase == TickEvent.Phase.START) {
                IMCheckState.clientTick(new ForgeScreenInfo());
            }
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onConfigLoadReload(ModConfigEvent e) {
            Common.LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
            ForgeConfig.reload();
        }
    }
}
