package io.github.reserveword.imblocker;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

final class ModLoaderClientHooks {

	private ModLoaderClientHooks() {}

	static void registerClientTickEvent(Runnable tickEvent) {
		NeoForge.EVENT_BUS.register(new Object() {
			@SubscribeEvent
			public void onStartTick(ClientTickEvent.Pre e) {
				tickEvent.run();
			}
		});
	}
}
