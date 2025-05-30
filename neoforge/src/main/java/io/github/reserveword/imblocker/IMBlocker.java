package io.github.reserveword.imblocker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.DetectedVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.GsonHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {

	private static final ModLoaderAccessor modLoaderAccessor;
	
    public IMBlocker(ModContainer container) {
    	MinecraftClientAccessor.INSTANCE = new MinecraftClientAccessor() {
			@Override
			public void execute(Runnable runnable) {
				Minecraft.getInstance().execute(runnable);
			}
			
			@Override
			public Rectangle getWindowBounds() {
				Window gameWindow = Minecraft.getInstance().getWindow();
				return new Rectangle(gameWindow.getX(), gameWindow.getY(), 
						gameWindow.getWidth(), gameWindow.getHeight());
			}
			
			@Override
			public int getStringWidth(String text) {
				return Minecraft.getInstance().font.width(text);
			}
		};

        // Register ourselves for server and other game events we are interested in
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.clientSpec);
        
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
            Config.INSTANCE.reloadScreenWhitelist(NeoForgeConfig.CLIENT.screenWhitelist.get());
        }
    }
    
    public static ModLoaderAccessor getModLoaderAccessor() {
    	return modLoaderAccessor;
    }
    
    static {
    	int protocolVersion;
    	try(InputStream is = DetectedVersion.class.getResourceAsStream("/version.json");
    			InputStreamReader isr = new InputStreamReader(is)) {
    		protocolVersion = GsonHelper.getAsInt(GsonHelper.parse(isr), "protocol_version");
    	} catch (Exception e) {
    		Common.LOGGER.warn("Failed to get protocol version!");
    		e.printStackTrace();
    		protocolVersion = Integer.MAX_VALUE;
		}
    	int currentProtocolVersion = protocolVersion;
    	
    	modLoaderAccessor = new ModLoaderAccessor() {
			@Override
			public boolean isGameVersionReached(int protocolVersion) {
				return currentProtocolVersion >= protocolVersion;
			}
			
			@Override
			public boolean hasMod(String modid) {
				return FMLLoader.getLoadingModList().getModFileById(modid) != null;
			}
			
			@Override
			public Mapping getMapping() {
				return Mapping.OFFICIAL;
			}
		};
    }
}
