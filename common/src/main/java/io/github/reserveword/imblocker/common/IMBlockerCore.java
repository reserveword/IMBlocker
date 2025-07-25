package io.github.reserveword.imblocker.common;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor.Mapping;
import me.decce.ixeris.api.IxerisApi;

public class IMBlockerCore {
	public static final String MODID = "imblocker";
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static final ModLoaderAccessor modLoaderAccessor;
	
	private static final boolean IS_IXERIS_LOADED;
	
	private static final Set<Runnable> deferredRunnables = new LinkedHashSet<>();
	
	public static void invokeOnMainThread(Runnable runnable) {
		if(IS_IXERIS_LOADED) {
			try {
				IxerisApi.getInstance().runLaterOnMainThread(runnable);
			} catch (Exception e) {
				LOGGER.fatal("[IMBlocker] Ixeris incompatible! Please report it to developer: {}", e);
			}
		}else {
			MinecraftClientAccessor.INSTANCE.execute(runnable);
		}
	}
	
	public static synchronized void invokeLater(Runnable runnable) {
		deferredRunnables.add(runnable);
	}
	
	public static synchronized void renderStart() {
		deferredRunnables.forEach(Runnable::run);
		deferredRunnables.clear();
	}
	
	public static boolean isGameVersionReached(int protocolVersion) {
		return modLoaderAccessor.isGameVersionReached(protocolVersion);
	}
	
	public static boolean hasMod(String modid) {
		return modLoaderAccessor.hasMod(modid);
	}
	
	public static Mapping getMapping() {
		return modLoaderAccessor.getMapping();
	}
	
	static {
		Class<?> modLoaderAccessorCls = null;
		try {
			modLoaderAccessorCls = Class.forName("io.github.reserveword.imblocker.ModLoaderAccessorImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		modLoaderAccessor = (ModLoaderAccessor) ReflectionUtil.newInstance(modLoaderAccessorCls, new Class[0]);
		IS_IXERIS_LOADED = modLoaderAccessor.hasMod("ixeris");
	}
}
