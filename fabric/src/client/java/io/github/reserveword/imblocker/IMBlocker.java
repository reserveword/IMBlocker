package io.github.reserveword.imblocker;

import org.lwjgl.glfw.GLFW;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Type;

public class IMBlocker implements ClientModInitializer {
	
	public static final KeyBinding unlockIMEKey = new KeyBinding(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.imblocker");
	
    @Override
    public void onInitializeClient() {
    	KeyBindingHelper.registerKeyBinding(unlockIMEKey);
		IMBlockerConfig.defaultScreenWhitelist.addAll(FabricCommon.defaultScreenWhitelist);
		if (hasClothConfig()) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
		} else {
			IMBlockerConfig.INSTANCE.reloadScreenWhitelist(IMBlockerConfig.defaultScreenWhitelist);
		}
	}

	public static boolean hasClothConfig() {
		return IMBlockerCore.hasMod("cloth-config") || IMBlockerCore.hasMod("cloth-config2");
	}
}
