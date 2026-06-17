package io.github.reserveword.imblocker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

final class ModLoaderClientHooks {

	private ModLoaderClientHooks() {}

	static void registerClientTickEvent(Runnable tickEvent) {
		MinecraftForge.EVENT_BUS.register(new Object() {
			@SubscribeEvent
			public void onStartTick(TickEvent.ClientTickEvent e) {
				if(e.phase == Phase.START) {
					tickEvent.run();
				}
			}
		});
	}
}
