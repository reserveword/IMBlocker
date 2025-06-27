package io.github.reserveword.imblocker;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlockerCore.MODID)
public class IMBlocker {
	
    public IMBlocker(ModContainer container) {
    	MinecraftClientAccessor.INSTANCE = new MinecraftClientAccessor() {
    		@Override
    		public void sendSafeKeyForFocusTracking(int key, int scancode) {
    			Minecraft client = Minecraft.getInstance();
				client.keyboardHandler.keyPress(client.getWindow().getWindow(), key, scancode, 1, 0);
    		}
    		
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
			public Object getCurrentScreen() {
				return Minecraft.getInstance().screen;
			}
			
			@Override
			public int getStringWidth(String text) {
				return Minecraft.getInstance().font.width(text);
			}
			
			@Override
			public void registerClientTickEvent(Runnable tickEvent) {
				NeoForge.EVENT_BUS.register(new Object() {
					@SubscribeEvent
					public void onStartTick(ClientTickEvent.Pre e) {
						tickEvent.run();
					}
				});
			}
		};

		IMBlockerConfig.defaultScreenWhitelist.addAll(ForgeCommon.defaultScreenWhitelist);
		if(IMBlockerCore.hasMod("cloth_config")) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
			container.registerExtensionPoint(IConfigScreenFactory.class, new IConfigScreenFactory() {
				public Screen createScreen(Minecraft minecraft, Screen modListScreen) {
					return IMBlockerAutoConfig.getConfigScreen(modListScreen, Screen.class);
				}

				@SuppressWarnings("unused")
				public Screen createScreen(ModContainer container, Screen modListScreen) {
					return IMBlockerAutoConfig.getConfigScreen(modListScreen, Screen.class);
				}
			});
		}else {
			IMBlockerConfig.INSTANCE = new IMBlockerConfig();
			IMBlockerConfig.INSTANCE.reloadScreenWhitelist(IMBlockerConfig.defaultScreenWhitelist);
		}
	}
}
