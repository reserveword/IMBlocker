package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class IMBlockerFabric implements ClientModInitializer {
	
    @Override
    public void onInitializeClient() {
		MinecraftClientAccessor.instance = new MinecraftClientAccessor() {
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
		
        if (hasMod("cloth-config") && hasMod("modmenu")) {
            AutoConfig.register(FabricConfig.class, GsonConfigSerializer::new);
            Config.INSTANCE = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
        } else {
            Config.INSTANCE = new Config() {
                @Override
                public boolean inScreenWhitelist(Class<?> cls) {
                    if (cls == null) {
                        return false;
                    }
                    return FabricCommon.defaultScreenWhitelist.contains(cls.getName());
                }
                
                @Override
                public ChatCommandInputType getChatCommandInputType() {
                	return ChatCommandInputType.IM_ENG_STATE;
                }
            };
        }
    }

    private boolean hasMod(String modid) {
        for (ModContainer mod: FabricLoader.getInstance().getAllMods()) {
            if (modid.equals(mod.getMetadata().getId())) {
                return true;
            }
        }
        return false;
    }
}
