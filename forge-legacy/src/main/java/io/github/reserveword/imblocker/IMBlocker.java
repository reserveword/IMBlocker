package io.github.reserveword.imblocker;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.mixin.KeyboardHandlerAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlockerCore.MODID)
public class IMBlocker {

	public IMBlocker() {
		this(FMLJavaModLoadingContext.get());
	}
	
    public IMBlocker(FMLJavaModLoadingContext context) {
		MinecraftClientAccessor.INSTANCE = new MinecraftClientAccessor() {
			@Override
			public void sendSafeCharForFocusTracking(int codePoint) {
				Minecraft client = Minecraft.getInstance();
				((KeyboardHandlerAccessor) client.keyboardHandler).invokeCharTyped(
						client.getWindow().getWindow(), codePoint, 0);
			}
			
			@Override
			public void execute(Runnable runnable) {
				Minecraft.getInstance().execute(runnable);
			}
			
			@Override
			public Rectangle getWindowBounds() {
				MainWindow gameWindow = Minecraft.getInstance().getWindow();
				int[] width = new int[1], height = new int[1];
				GLFW.glfwGetWindowSize(gameWindow.getWindow(), width, height);
				return new Rectangle(gameWindow.getX(), gameWindow.getY(), width[0], height[0]);
			}
			
			@Override
			public Dimension getContentSize() {
				MainWindow gameWindow = Minecraft.getInstance().getWindow();
				return new Dimension(gameWindow.getWidth(), gameWindow.getHeight());
			}
			
			@Override
			public Object getCurrentScreen() {
				return Minecraft.getInstance().screen;
			}
			
			@Override
			public int getStringWidth(String text) {
				return Minecraft.getInstance().font.width(text);
			}
			
			public void registerClientTickEvent(Runnable tickEvent) {
				MinecraftForge.EVENT_BUS.register(new Object() {
					@SubscribeEvent
					public void onStartTick(TickEvent.ClientTickEvent e) {
						if(e.phase == Phase.START) {
							tickEvent.run();
						}
					}
				});
			}
		};
		
		IMBlockerConfig.defaultScreenWhitelist.addAll(Arrays.asList(
				EditBookScreen.class.getName(),
				EditSignScreen.class.getName()));
		if(IMBlockerCore.hasMod("cloth-config")) {
            AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
            IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, 
					() -> (client, parent) -> IMBlockerAutoConfig.getConfigScreen(parent, Screen.class));
		}else {
            IMBlockerConfig.INSTANCE.reloadScreenWhitelist(IMBlockerConfig.defaultScreenWhitelist);
		}
    }
}
