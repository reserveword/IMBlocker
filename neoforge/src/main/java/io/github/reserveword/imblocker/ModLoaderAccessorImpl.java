package io.github.reserveword.imblocker;

import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import net.minecraft.DetectedVersion;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.loading.FMLLoader;

public class ModLoaderAccessorImpl implements ModLoaderAccessor {

	private static final int currentProtocolVersion;
	
	private ModLoaderAccessorImpl() {}
	
	@Override
	public boolean isGameVersionReached(int protocolVersion) {
		return currentProtocolVersion >= protocolVersion;
	}
	
	@Override
	public boolean hasMod(String modid) {
		return FMLLoader.getLoadingModList().getModFileById(modid) != null;
	}
	
	@Override
	public Mapping getMapping() {
		return Mapping.OFFICIAL;
	}
	
	static {
		int protocolVersion;
		try (InputStream is = DetectedVersion.class.getResourceAsStream("/version.json");
				InputStreamReader isr = new InputStreamReader(is)) {
			protocolVersion = GsonHelper.getAsInt(GsonHelper.parse(isr), "protocol_version");
		} catch (Exception e) {
			IMBlockerCore.LOGGER.warn("[IMBlocker] Failed to get protocol version!");
			protocolVersion = Integer.MAX_VALUE;
		}
		currentProtocolVersion = protocolVersion;
	}
}
