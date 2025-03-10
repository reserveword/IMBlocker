package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.MainThreadExecutor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
    public IMBlocker(FMLJavaModLoadingContext context) {
    	MainThreadExecutor.instance = new MainThreadExecutor() {
			@Override
			public void execute(Runnable runnable) {
				Minecraft.getInstance().execute(runnable);
			}
		};

        // Register ourselves for server and other game events we are interested in
        context.getModEventBus().addListener(this::onConfigLoadReload);
        context.registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
    }

    @SubscribeEvent
    public void onConfigLoadReload(ModConfigEvent e) {
        Common.LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
        ForgeConfig.reload();
    }
}
