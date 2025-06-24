package io.github.reserveword.imblocker.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor.Mapping;

public class Common {
	public static final String MODID = "imblocker";
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static final ModLoaderAccessor modLoaderAccessor;
	
	public static boolean isTrackingFocus = false;
	
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
	}
}
