package io.github.reserveword.imblocker;

import java.lang.reflect.Constructor;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.MainThreadExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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
        
        try {
			@SuppressWarnings("unchecked")
			Class<? extends Screen> configurationScreenClass = (Class<? extends Screen>) 
					Class.forName("net.neoforged.neoforge.client.gui.ConfigurationScreen");
			Constructor<? extends Screen> configurationScreenConstructor = configurationScreenClass
					.getDeclaredConstructor(ModContainer.class, Screen.class);
	        container.registerExtensionPoint(IConfigScreenFactory.class, new IConfigScreenFactory() {
				public Screen createScreen(Minecraft minecraft, Screen modListScreen) { return null; }
				public Screen createScreen(ModContainer container, Screen modListScreen) {
					return createInstance(configurationScreenConstructor, container, modListScreen);
				}
			});
		} catch (ClassNotFoundException e) {
			Common.LOGGER.warn("Configuration screen is not available, which only supports 1.21+");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private static <T> T createInstance(Constructor<T> constructor, Object... args) {
    	try {
			return constructor.newInstance(args);
		} catch (Exception e) {
			return null;
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
