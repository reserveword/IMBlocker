package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import imgui.callback.ImGuiInputTextCallback;
import imgui.internal.ImGui;
import imgui.type.ImString;
import io.github.reserveword.imblocker.IMCheckState;

@Pseudo
@Mixin(targets = "imgui.ImGui")
public abstract class ImGuiMixin {
	private static boolean hasFocusedWidget = false;
	
	@Inject(method = "preInputText(ZLjava/lang/String;Ljava/lang/String;Limgui/type/ImString;"
			+ "FFILimgui/callback/ImGuiInputTextCallback;)Z", at = @At("HEAD"))
	private static void preInputText(boolean multiline, String label, String hint, ImString text, float width,
			float height, int flags, ImGuiInputTextCallback callback, CallbackInfoReturnable<Boolean> ci) {
		boolean wantTextInput = ImGui.getIO().getWantTextInput();
		if(hasFocusedWidget != wantTextInput) {
			if(wantTextInput) {
				IMCheckState.imguiFocused = true;
			}else {
				IMCheckState.imguiFocused = false;
			}
			hasFocusedWidget = wantTextInput;
		}
	}
}
