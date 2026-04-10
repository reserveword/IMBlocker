package io.github.reserveword.imblocker.common.accessor;

import java.util.List;

public interface ModLoaderAccessor {

	boolean hasMod(String modid);
	boolean isGameVersionReached(int protocolVersion);
	Mapping getMapping();
	void registerIMERenderMixin(List<String> validMixins);
	void registerClientTickEvent(Runnable tickEvent);
	
	public enum Mapping {
		OFFICIAL, YARN
	}
}
