package io.github.reserveword.imblocker;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MODID)
public class IMBlocker {
	
	private static int currentProtocolVersion;

	public IMBlocker() {
		this(FMLJavaModLoadingContext.get());
	}
	
    public IMBlocker(FMLJavaModLoadingContext context) {
		MinecraftClientAccessor.instance = new MinecraftClientAccessor() {
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
        context.getModEventBus().addListener(this::onConfigLoadReload);
        try {
            context.registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
		} catch (NoSuchMethodError e) {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
		}
    }

    @SubscribeEvent
    public void onConfigLoadReload(ModConfigEvent e) {
        Common.LOGGER.info("imblock {}loading config", (e instanceof ModConfigEvent.Reloading)?"re":"");
        ForgeConfig.reload();
    }
    
    public static boolean isGameVersionReached(int protocolVersion) {
    	return currentProtocolVersion >= protocolVersion;
    }
    
    static {
    	try(InputStream is = IMBlocker.class.getResourceAsStream("/version.json");
    			InputStreamReader isr = new InputStreamReader(is)) {
    		currentProtocolVersion = GsonHelper.getAsInt(GsonHelper.parse(isr), "protocol_version");
    	} catch (Exception e) {
    		Common.LOGGER.warn("Failed to get protocol version!");
    		currentProtocolVersion = Integer.MAX_VALUE;
		}
    }
}
