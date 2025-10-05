package io.github.reserveword.imblocker.common;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.mixin.KeyboardHandlerAccessor;
import net.minecraft.DetectedVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.util.GsonHelper;

public abstract class MinecraftClientUtil {
	
	private static final int currentProtocolVersion;
	
	public static boolean isGameVersionReached(int protocolVersion) {
		return currentProtocolVersion >= protocolVersion;
	}
	
	public static void sendSafeCharForFocusTracking(int codePoint) {
		Minecraft client = Minecraft.getInstance();
		((KeyboardHandlerAccessor) client.keyboardHandler).invokeCharTyped(
				client.getWindow().handle(), new CharacterEvent(codePoint, 0));
	}
	
	public static Rectangle getWindowBounds() {
		Window gameWindow = Minecraft.getInstance().getWindow();
		int[] width = new int[1], height = new int[1];
		GLFW.glfwGetWindowSize(gameWindow.handle(), width, height);
		return new Rectangle(gameWindow.getX(), gameWindow.getY(), width[0], height[0]);
	}
	
	public static Dimension getContentSize() {
		Window gameWindow = Minecraft.getInstance().getWindow();
		return new Dimension(gameWindow.getWidth(), gameWindow.getHeight());
	}
	
	public static int getStringWidth(String text) {
		return Minecraft.getInstance().font.width(text);
	}
	
	static {
		int protocolVersion;
		try (InputStream is = DetectedVersion.class.getResourceAsStream("/version.json");
				InputStreamReader isr = new InputStreamReader(is)) {
			protocolVersion = GsonHelper.getAsInt(GsonHelper.parse(isr), "protocol_version");
		} catch (Exception e) {
			IMBlockerCore.LOGGER.warn("[IMBlocker] Failed to get protocol version!");
			protocolVersion = Integer.MAX_VALUE;
		}
		currentProtocolVersion = protocolVersion;
	}
}
