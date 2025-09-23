package io.github.reserveword.imblocker;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlockerCore.MODID)
public class IMBlocker {
	
	public static final KeyBinding unlockIMEKey = new KeyBinding(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.imblocker");

	public IMBlocker() {
		this(FMLJavaModLoadingContext.get());
	}
	
    public IMBlocker(FMLJavaModLoadingContext context) {
    	ClientRegistry.registerKeyBinding(unlockIMEKey);
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
