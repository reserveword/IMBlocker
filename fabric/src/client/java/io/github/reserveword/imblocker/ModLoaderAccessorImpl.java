package io.github.reserveword.imblocker;

import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.JsonHelper;

public class ModLoaderAccessorImpl implements ModLoaderAccessor {
	
	private static final int currentProtocolVersion;
	
	private ModLoaderAccessorImpl() {}

	@Override
	public boolean isGameVersionReached(int protocolVersion) {
		return currentProtocolVersion > protocolVersion;
	}
	
	@Override
	public boolean hasMod(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}
	
	@Override
	public Mapping getMapping() {
		return Mapping.YARN;
	}
	
	static {
		int protocolVersion;
		try (InputStream is = IMBlocker.class.getResourceAsStream("/version.json");
				InputStreamReader isr = new InputStreamReader(is)) {
			protocolVersion = JsonHelper.getInt(JsonHelper.deserialize(isr), "protocol_version");
		} catch (Exception e) {
			IMBlockerCore.LOGGER.warn("Failed to get protocol version!");
			protocolVersion = Integer.MAX_VALUE;
		}
		currentProtocolVersion = protocolVersion;
	}
}
