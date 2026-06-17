package io.github.reserveword.imblocker;

import java.util.Collections;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Type;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class IMBlockerClient {

	public static final KeyMapping unlockIMEKey = new KeyMapping(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.imblocker");

	private IMBlockerClient() {}

	public static void initialize(ModContainer container) {
		container.getEventBus().register(new Object() {
			@SubscribeEvent
			public void registerKeyBindings(RegisterKeyMappingsEvent event) {
				event.register(unlockIMEKey);
			}
		});

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
			IMBlockerConfig.INSTANCE.reloadScreenWhitelist(Collections.emptyList());
		}
	}
}
