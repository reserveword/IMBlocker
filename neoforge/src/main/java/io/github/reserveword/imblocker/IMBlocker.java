package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.MainThreadExecutor;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
    public IMBlocker(ModContainer container) {
    	MainThreadExecutor.instance = new MainThreadExecutor() {
			@Override
			public void execute(Runnable runnable) {
				Minecraft.getInstance().execute(runnable);
			}
		};

        // Register ourselves for server and other game events we are interested in
        container.registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
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
