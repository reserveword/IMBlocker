package io.github.reserveword.imblocker;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Type;

public class IMBlockerKeyBindings {
	public static final KeyBinding unlockIMEKey = new KeyBinding(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.imblocker");
}
