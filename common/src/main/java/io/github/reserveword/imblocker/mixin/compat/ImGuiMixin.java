package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import imgui.ImGui;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import io.github.reserveword.imblocker.common.gui.GenericAxiomTextField;

@Mixin(ImGui.class)
public class ImGuiMixin {
	
	@ModifyVariable(method = "preInputText(ZLjava/lang/String;Ljava/lang/String;"
			+ "Limgui/type/ImString;FFILimgui/callback/ImGuiInputTextCallback;)",
			at = @At("HEAD"), ordinal = 0)
	private static int enableCallbacks(int flagsV) {
		return flagsV | ImGuiInputTextFlags.CallbackAlways;
	}
	
	@ModifyVariable(method = "preInputText(ZLjava/lang/String;Ljava/lang/String;"
			+ "Limgui/type/ImString;FFILimgui/callback/ImGuiInputTextCallback;)",
			at = @At("HEAD"), ordinal = 0)
	private static ImGuiInputTextCallback attachTextCallback(ImGuiInputTextCallback callback) {
		return GenericAxiomTextField.getAxiomTextFieldCallback(callback);
	}
}
