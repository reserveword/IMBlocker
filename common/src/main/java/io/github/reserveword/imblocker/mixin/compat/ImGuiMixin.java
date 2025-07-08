package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import imgui.ImGui;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import io.github.reserveword.imblocker.common.gui.GenericAxiomTextField;

@Pseudo
@Mixin(value = ImGui.class, remap = false)
public class ImGuiMixin {
	
	private static final String preInputTextMethodDescriptor = 
			"preInputText(ZLjava/lang/String;Ljava/lang/String;Limgui/type/ImString;FFILimgui/callback/ImGuiInputTextCallback;)Z";
	
	@Inject(method = preInputTextMethodDescriptor, at = @At("HEAD"))
	private static void captureArgs(boolean multiline, String label, String hint, 
			ImString text, float width, float height, int flagsV, 
			ImGuiInputTextCallback callback, CallbackInfoReturnable<Boolean> cir) {
		GenericAxiomTextField.setMultiline(multiline);
		GenericAxiomTextField.setLabel(label);
	}
	
	@ModifyVariable(method = preInputTextMethodDescriptor, at = @At("HEAD"), ordinal = 0)
	private static int enableCallbacks(int flagsV) {
		flagsV |= ImGuiInputTextFlags.CallbackAlways;
		GenericAxiomTextField.setInputTextFlags(flagsV);
		return flagsV;
	}
	
	@ModifyVariable(method = preInputTextMethodDescriptor, at = @At("HEAD"), ordinal = 0)
	private static ImGuiInputTextCallback attachTextCallback(ImGuiInputTextCallback callback) {
		return GenericAxiomTextField.getAxiomTextFieldCallback(callback);
	}
}
