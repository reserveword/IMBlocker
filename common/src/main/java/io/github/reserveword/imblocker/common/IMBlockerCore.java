package io.github.reserveword.imblocker.common;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.ftb.mods.ftblibrary.client.gui.widget.ScreenWrapper;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import me.decce.ixeris.api.IxerisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class IMBlockerCore {
	public static final String MODID = "imblocker";
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static final ModLoaderAccessor modLoaderAccessor;
	
	public static final boolean IS_SDL_PRESENT;
	
	private static final boolean IS_IXERIS_LOADED;
	private static final boolean IS_FTBLIB_LOADED;
	
	private static final String IXERIS_INCOMPAT_MSG = "[IMBlocker] Ixeris incompatible! Please report it to developer: ";
	
	private static final Set<Runnable> deferredRunnables = new LinkedHashSet<>();
	
	public static void invokeOnMainThread(Runnable runnable) {
		if(IS_IXERIS_LOADED) {
			try {
				IxerisApi.getInstance().runLaterOnMainThread(runnable);
			} catch (Throwable e) {
				throw new RuntimeException(IXERIS_INCOMPAT_MSG, e);
			}
		}else {
			vanillaExecute(runnable);
		}
	}
	
	public static void invokeAsyncOnMainThread(Runnable runnable) {
		if(IS_IXERIS_LOADED) {
			try {
				IxerisApi.getInstance().runNowOnMainThread(runnable);
			} catch (Throwable e) {
				throw new RuntimeException(IXERIS_INCOMPAT_MSG, e);
			}
		}else {
			vanillaExecute(runnable);
		}
	}
	
	public static void invokeOnRenderThread(Runnable runnable) {
		if(IS_IXERIS_LOADED) {
			try {
				IxerisApi.getInstance().runLaterOnRenderThread(runnable);
			} catch (Throwable e) {
				throw new RuntimeException(IXERIS_INCOMPAT_MSG, e);
			}
		}else {
			vanillaExecute(runnable);
		}
	}
	
	private static void vanillaExecute(Runnable runnable) {
		if(Minecraft.getInstance().isSameThread()) {
			runnable.run();
		}else {
			Minecraft.getInstance().execute(runnable);
		}
	}
	
	public static synchronized void invokeLater(Runnable runnable) {
		deferredRunnables.add(runnable);
	}
	
	public static synchronized void tickStart() {
		deferredRunnables.forEach(Runnable::run);
		deferredRunnables.clear();
	}
	
	public static boolean hasMod(String modid) {
		return modLoaderAccessor.hasMod(modid);
	}
	
	public static boolean isFTBScreen(Screen screen) {
		return IS_FTBLIB_LOADED && screen instanceof ScreenWrapper;
	}
	
	public static void registerClientTickEvent(Runnable tickEvent) {
		modLoaderAccessor.registerClientTickEvent(tickEvent);
	}
	
	static {
		Class<?> modLoaderAccessorCls = null;
		try {
			modLoaderAccessorCls = Class.forName("io.github.reserveword.imblocker.ModLoaderAccessorImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		modLoaderAccessor = (ModLoaderAccessor) ReflectionUtil.newInstance(modLoaderAccessorCls, new Class[0]);
		IS_SDL_PRESENT = MinecraftClientUtil.isGameVersionReached(777/*26.3*/) || modLoaderAccessor.hasMod("blazesdl");
		IS_IXERIS_LOADED = modLoaderAccessor.hasMod("ixeris");
		IS_FTBLIB_LOADED = modLoaderAccessor.hasMod("ftblibrary");
	}
}
