package io.github.reserveword.imblocker;

import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.JsonHelper;

public class IMBlocker implements ClientModInitializer {
	
	private static final ModLoaderAccessor modLoaderAccessor;
	
    @Override
    public void onInitializeClient() {
		MinecraftClientAccessor.INSTANCE = new MinecraftClientAccessor() {
			@Override
			public void execute(Runnable runnable) {
				MinecraftClient.getInstance().execute(runnable);
			}
			
			@Override
			public Rectangle getWindowBounds() {
				Window gameWindow = MinecraftClient.getInstance().getWindow();
				return new Rectangle(gameWindow.getX(), gameWindow.getY(), 
						gameWindow.getFramebufferWidth(), gameWindow.getFramebufferHeight());
			}
			
			@Override
			public int getStringWidth(String text) {
				return MinecraftClient.getInstance().textRenderer.getWidth(text);
			}
		};
		
        if (hasClothConfig()) {
            AutoConfig.register(FabricConfig.class, GsonConfigSerializer::new);
            Config.INSTANCE = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
        } else {
            Config.INSTANCE = new Config() {
                @Override
                public ChatCommandInputType getChatCommandInputType() {
                	return ChatCommandInputType.IM_ENG_STATE;
                }

				public void recoverScreen(String screenClsName) {}
            };
            Config.INSTANCE.reloadScreenWhitelist(FabricCommon.defaultScreenWhitelist);
        }
    }
    
    public static boolean hasClothConfig() {
    	return modLoaderAccessor.hasMod("cloth-config") || modLoaderAccessor.hasMod("cloth-config2");
    }

    public static ModLoaderAccessor getModLoaderAccessor() {
    	return modLoaderAccessor;
    }
    
    static {
    	int protocolVersion;
    	try(InputStream is = IMBlocker.class.getResourceAsStream("/version.json");
    			InputStreamReader isr = new InputStreamReader(is)) {
    		protocolVersion = JsonHelper.getInt(JsonHelper.deserialize(isr), "protocol_version");
    	} catch (Exception e) {
    		Common.LOGGER.warn("Failed to get protocol version!");
    		protocolVersion = Integer.MAX_VALUE;
		}
    	int currentProtocolVersion = protocolVersion;
    	
    	modLoaderAccessor = new ModLoaderAccessor() {
			@Override
			public boolean isGameVersionReached(int protocolVersion) {
				return currentProtocolVersion > protocolVersion;
			}
			
			@Override
			public boolean hasMod(String modid) {
				return FabricLoader.getInstance().isModLoaded(modid);
			}
			
			@Override
			public Mapping getMapping() {
				return Mapping.YARN;
			}
		};
    }
}
