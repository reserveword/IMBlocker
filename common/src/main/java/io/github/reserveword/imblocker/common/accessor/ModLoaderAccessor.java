package io.github.reserveword.imblocker.common.accessor;

public interface ModLoaderAccessor {
	boolean hasMod(String modid);
	void registerClientTickEvent(Runnable tickEvent);
}
