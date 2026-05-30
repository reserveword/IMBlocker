package io.github.reserveword.imblocker;

import java.util.Collections;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMBlockerKeyBindings;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class IMBlocker implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		KeyMappingHelper.registerKeyMapping(IMBlockerKeyBindings.unlockIMEKey);
		if(hasClothConfig()) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
		}else {
			IMBlockerConfig.INSTANCE.reloadScreenWhitelist(Collections.emptyList());
		}
	}

	public static boolean hasClothConfig() {
		return IMBlockerCore.hasMod("cloth-config");
	}
}