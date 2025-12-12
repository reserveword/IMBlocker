package io.github.reserveword.imblocker.common;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.ResourceLocation;

public final class IMBlockerKeyBindings {
	public static final KeyMapping unlockIMEKey = new KeyMapping(
			"key.unlockIME", Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, createCategory());
	
	private static Category createCategory() {
		try {
			return Category.register(ResourceLocation.fromNamespaceAndPath("imblocker", "ime"));
		} catch (Throwable e) {
			try {
				//1.21.11+ unobf.
				Class<?> resourceCls = Class.forName("net.minecraft.resources.Identifier");
				return ReflectionUtil.invokeMethod(Category.class, null, Category.class, "register", 
						new Class[] {resourceCls}, 
						ReflectionUtil.invokeMethod(resourceCls, null, resourceCls, "fromNamespaceAndPath", 
								new Class[] {String.class, String.class}, "imblocker", "ime"));
			} catch (Throwable e2) {
				throw new RuntimeException();
			}
		}
	}
}
