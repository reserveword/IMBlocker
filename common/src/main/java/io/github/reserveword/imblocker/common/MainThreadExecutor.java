package io.github.reserveword.imblocker.common;

public abstract class MainThreadExecutor {
	
	public static MainThreadExecutor instance;
	
	public abstract void execute(Runnable runnable);
}
