package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class IMBlocker implements ClientModInitializer {
	
    @Override
    public void onInitializeClient() {
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
