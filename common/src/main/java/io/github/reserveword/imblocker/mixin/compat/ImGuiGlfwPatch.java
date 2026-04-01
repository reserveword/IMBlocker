package io.github.reserveword.imblocker.mixin.compat;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.moulberry.axiom.editor.CustomImGuiImplGlfw", remap = false)
public abstract class ImGuiGlfwPatch {
	@Redirect(method = "init", at = @At(value = "INVOKE", target = 
			"glfwSetCharModsCallback(JLorg/lwjgl/glfw/GLFWCharModsCallbackI;)Lorg/lwjgl/glfw/GLFWCharModsCallback;"))
	public GLFWCharModsCallback correctCharCallback(long window, GLFWCharModsCallbackI cbfun) {
		GLFWCharCallback vanillaCharCallback = GLFW.glfwSetCharCallback(window, (w, c) -> cbfun.invoke(window, c, 0));
		return GLFWCharModsCallback.create((w, c, m) -> vanillaCharCallback.invoke(w, c));
	}
}
