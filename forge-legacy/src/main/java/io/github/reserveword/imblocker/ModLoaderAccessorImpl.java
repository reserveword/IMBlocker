package io.github.reserveword.imblocker;

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
}
