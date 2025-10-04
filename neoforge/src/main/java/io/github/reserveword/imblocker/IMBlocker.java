package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMBlockerKeyBindings;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(IMBlockerCore.MODID)
public class IMBlocker {
	
	public IMBlocker(ModContainer container) {
		container.getEventBus().register(new Object() {
			@SubscribeEvent
    		public void registerKeyBindings(RegisterKeyMappingsEvent event) {
    			event.register(IMBlockerKeyBindings.unlockIMEKey);
    		}
		});
		
		if(IMBlockerCore.hasMod("cloth_config")) {
			AutoConfig.register(IMBlockerAutoConfig.class, GsonConfigSerializer::new);
			IMBlockerConfig.INSTANCE = AutoConfig.getConfigHolder(IMBlockerAutoConfig.class).getConfig();
			container.registerExtensionPoint(IConfigScreenFactory.class, 
					(modContainer, modListScreen) -> IMBlockerAutoConfig.getConfigScreen(modListScreen));
		}
	}
}
