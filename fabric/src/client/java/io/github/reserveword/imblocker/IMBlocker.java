package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.mixin.KeyboardAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class IMBlocker implements ClientModInitializer {
	
    @Override
    public void onInitializeClient() {
		MinecraftClientAccessor.INSTANCE = new MinecraftClientAccessor() {
			@Override
			public void sendSafeCharForFocusTracking() {
				MinecraftClient client = MinecraftClient.getInstance();
				((KeyboardAccessor) client.keyboard).invokeOnChar(
						client.getWindow().getHandle(), 1, 0);
			}
			
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
		
		IMBlockerConfig.defaultScreenWhitelist.addAll(FabricCommon.defaultScreenWhitelist);
		if (hasClothConfig()) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
		} else {
			IMBlockerConfig.INSTANCE = new IMBlockerConfig();
			IMBlockerConfig.INSTANCE.reloadScreenWhitelist(IMBlockerConfig.defaultScreenWhitelist);
		}
	}

	public static boolean hasClothConfig() {
		return IMBlockerCore.hasMod("cloth-config") || IMBlockerCore.hasMod("cloth-config2");
	}
}
