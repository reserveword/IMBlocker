package io.github.reserveword.imblocker;

import org.lwjgl.glfw.GLFW;

import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.mixin.KeyboardAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class MinecraftClientAccessorImpl extends MinecraftClientAccessor {
	
	private MinecraftClientAccessorImpl() {}
	
	@Override
	public void sendSafeCharForFocusTracking(int codePoint) {
		MinecraftClient client = MinecraftClient.getInstance();
		((KeyboardAccessor) client.keyboard).invokeOnChar(
				client.getWindow().getHandle(), codePoint, 0); 
	}
	
	@Override
	public void execute(Runnable runnable) {
		MinecraftClient.getInstance().execute(runnable);
	}
	
	@Override
	public Rectangle getWindowBounds() {
		Window gameWindow = MinecraftClient.getInstance().getWindow();
		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetWindowSize(gameWindow.getHandle(), width, height);
		return new Rectangle(gameWindow.getX(), gameWindow.getY(), width[0], height[0]);
	}
	
	@Override
	public Dimension getContentSize() {
		Window gameWindow = MinecraftClient.getInstance().getWindow();
		return new Dimension(gameWindow.getFramebufferWidth(), gameWindow.getFramebufferHeight());
	}
	
	@Override
	public Object getCurrentScreen() {
		return MinecraftClient.getInstance().currentScreen;
	}
	
	@Override
	public int getStringWidth(String text) {
		return MinecraftClient.getInstance().textRenderer.getWidth(text);
	}
}
