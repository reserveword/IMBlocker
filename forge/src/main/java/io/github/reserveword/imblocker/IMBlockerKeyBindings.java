package io.github.reserveword.imblocker;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;

public class IMBlockerKeyBindings {
	public static final KeyMapping unlockIMEKey = new KeyMapping(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.imblocker");
}
