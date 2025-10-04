package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModLoaderAccessorImpl implements ModLoaderAccessor {
	
	private ModLoaderAccessorImpl() {}
	
	@Override
	public boolean hasMod(String modid) {
		return FMLLoader.getCurrent().getLoadingModList().getModFileById(modid) != null;
	}
	
	@Override
	public void registerClientTickEvent(Runnable tickEvent) {
		NeoForge.EVENT_BUS.register(new Object() {
			@SubscribeEvent
			public void onStartTick(ClientTickEvent.Pre e) {
				tickEvent.run();
			}
		});
	}
}
