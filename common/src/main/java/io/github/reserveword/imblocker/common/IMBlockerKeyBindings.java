package io.github.reserveword.imblocker.common;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.ResourceLocation;

public final class IMBlockerKeyBindings {
	public static final KeyMapping unlockIMEKey = new KeyMapping(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, 
			Category.register(ResourceLocation.fromNamespaceAndPath("imblocker", "ime")));
}
