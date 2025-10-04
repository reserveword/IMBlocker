package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public class ModLoaderAccessorImpl implements ModLoaderAccessor {
	
	private ModLoaderAccessorImpl() {}
	
	@Override
	public boolean hasMod(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}
	
	@Override
	public void registerClientTickEvent(Runnable tickEvent) {
		ClientTickEvents.START_CLIENT_TICK.register(client -> tickEvent.run());
	}
}
