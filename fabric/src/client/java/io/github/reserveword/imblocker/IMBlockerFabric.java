package io.github.reserveword.imblocker;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class IMBlockerFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (hasMod("cloth-config") && hasMod("modmenu")) {
            AutoConfig.register(FabricConfig.class, GsonConfigSerializer::new);
            Config.INSTANCE = AutoConfig.getConfigHolder(FabricConfig.class).getConfig();
        } else {
            Config.INSTANCE = new Config() {
                @Override
                public boolean inScreenWhitelist(Class<?> cls) {
                    return FabricCommon.defaultScreenWhitelist.contains(cls.getName());
                }
            };
        }
        ClientTickEvents.START_CLIENT_TICK.register(tick -> {
            IMCheckState.clientTick(new FabricScreenInfo());
        });
        // TODO mouse click event
        // it needs to register for every screen INSTANCE so let's just mixin instead
        // ScreenMouseEvents.afterMouseClick()
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
