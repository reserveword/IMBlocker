package io.github.reserveword.imblocker.common.accessor;

public interface ModLoaderAccessor {

	boolean hasMod(String modid);
	boolean isGameVersionReached(int protocolVersion);
	Mapping getMapping();
	void registerClientTickEvent(Runnable tickEvent);
	
	public enum Mapping {
		OFFICIAL, YARN
	}
}
