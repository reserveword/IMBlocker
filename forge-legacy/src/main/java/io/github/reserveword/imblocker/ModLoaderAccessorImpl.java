package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;

public class ModLoaderAccessorImpl implements ModLoaderAccessor {
	
	private ModLoaderAccessorImpl() {}
	
	@Override
	public boolean isGameVersionReached(int protocolVersion) {
		return false;
	}
	
	@Override
	public boolean hasMod(String modid) {
		return FMLLoader.getLoadingModList().getModFileById(modid) != null;
	}
	
	@Override
	public Mapping getMapping() {
		return Mapping.OFFICIAL;
	}
	
	@Override
	public void registerClientTickEvent(Runnable tickEvent) {
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
