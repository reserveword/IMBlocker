package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlockerCore.MODID)
public class IMBlocker {
    public IMBlocker(ModContainer container) {
    	if(FMLEnvironment.dist != Dist.CLIENT) {
    		IMBlockerCore.printRunningOnServerWarning();
    		return;
    	}
    	
    	container.getEventBus().register(new Object() {
    		@SubscribeEvent
    		public void registerKeyBindings(RegisterKeyMappingsEvent event) {
    			event.register(IMBlockerKeyBindings.unlockIMEKey);
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
			IMBlockerConfig.INSTANCE.reloadConfig();
		}
	}
}
