package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMBlockerKeyBindings;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class IMBlocker implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(IMBlockerKeyBindings.unlockIMEKey);
		if(hasClothConfig()) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
		}
	}

	public static boolean hasClothConfig() {
		return IMBlockerCore.hasMod("cloth-config");
	}
}