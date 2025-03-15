package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.MainThreadExecutor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;

public class IMBlockerFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MainThreadExecutor.instance = new MainThreadExecutor() {
			@Override
			public void execute(Runnable runnable) {
				MinecraftClient.getInstance().execute(runnable);
			}
		};
		
        if (hasMod("cloth-config") && hasMod("modmenu")) {
            AutoConfig.register(FabricConfig.class, GsonConfigSerializer::new);
            Config.INSTANCE = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
        } else {
            Config.INSTANCE = new Config() {
                @Override
                public boolean inScreenWhitelist(Class<?> cls) {
                    if (cls == null) {
                        return false;
                    }
                    return FabricCommon.defaultScreenWhitelist.contains(cls.getName());
                }
            };
        }
    }

    private boolean hasMod(String modid) {
        for (ModContainer mod: FabricLoader.getInstance().getAllMods()) {
            if (modid.equals(mod.getMetadata().getId())) {
                return true;
            }
        }
        return false;
    }
}
