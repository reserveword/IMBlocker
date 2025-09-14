package io.github.reserveword.imblocker;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.mixin.KeyboardHandlerAccessor;
import net.minecraft.client.Minecraft;

public class MinecraftClientAccessorImpl extends MinecraftClientAccessor {
	
	private MinecraftClientAccessorImpl() {}
	
	@Override
	public void sendSafeCharForFocusTracking(int codePoint) {
		Minecraft client = Minecraft.getInstance();
		((KeyboardHandlerAccessor) client.keyboardHandler).invokeCharTyped(
				client.getWindow().getWindow(), codePoint, 0);
	}
	
	@Override
	public void execute(Runnable runnable) {
		Minecraft.getInstance().execute(runnable);
	}
	
	@Override
	public long getWindowHandle() {
		return Minecraft.getInstance().getWindow().getWindow();
	}
	
	@Override
	public Rectangle getWindowBounds() {
		Window gameWindow = Minecraft.getInstance().getWindow();
		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetWindowSize(gameWindow.getWindow(), width, height);
		return new Rectangle(gameWindow.getX(), gameWindow.getY(), width[0], height[0]);
	}
	
	@Override
	public Dimension getContentSize() {
		Window gameWindow = Minecraft.getInstance().getWindow();
		return new Dimension(gameWindow.getWidth(), gameWindow.getHeight());
	}
	
	@Override
	public Object getCurrentScreen() {
		return Minecraft.getInstance().screen;
	}
	
	@Override
	public int getStringWidth(String text) {
		return Minecraft.getInstance().font.width(text);
	}
}
