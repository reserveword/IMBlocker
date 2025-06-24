package io.github.reserveword.imblocker;

import java.util.Arrays;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
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
			public void sendSafeCharForFocusTracking() {
				Screen screen = Minecraft.getInstance().screen;
				if(screen == null || !screen.charTyped('\u0001', 0)) {
					FocusContainer.MINECRAFT.cancelFocus();
				}
			}
			
			@Override
			public void execute(Runnable runnable) {
				Minecraft.getInstance().execute(runnable);
			}
			
			@Override
			public Rectangle getWindowBounds() {
				MainWindow gameWindow = Minecraft.getInstance().getWindow();
				return new Rectangle(gameWindow.getX(), gameWindow.getY(), 
						gameWindow.getWidth(), gameWindow.getHeight());
			}
			
			@Override
			public int getStringWidth(String text) {
				return Minecraft.getInstance().font.width(text);
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
            IMBlockerConfig.INSTANCE = new IMBlockerConfig();
            IMBlockerConfig.INSTANCE.reloadScreenWhitelist(IMBlockerConfig.defaultScreenWhitelist);
		}
    }
}
