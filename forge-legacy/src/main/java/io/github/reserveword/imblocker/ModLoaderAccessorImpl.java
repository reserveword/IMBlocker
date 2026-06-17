package io.github.reserveword.imblocker;

import java.lang.reflect.Method;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
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
		registerClientTickEventReflectively(tickEvent);
	}

	private static void registerClientTickEventReflectively(Runnable tickEvent) {
		try {
			Method method = Class.forName("io.github.reserveword.imblocker.ModLoaderClientHooks")
					.getDeclaredMethod("registerClientTickEvent", Runnable.class);
			method.setAccessible(true);
			method.invoke(null, tickEvent);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to register IMBlocker client tick event", e);
		}
	}
}
