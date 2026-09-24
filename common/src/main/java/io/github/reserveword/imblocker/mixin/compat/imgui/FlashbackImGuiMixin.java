package io.github.reserveword.imblocker.mixin.compat.imgui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import imgui.moulberry90.ImGui;
import imgui.moulberry90.callback.ImGuiInputTextCallback;
import imgui.moulberry90.flag.ImGuiInputTextFlags;
import imgui.moulberry90.type.ImString;
import io.github.reserveword.imblocker.common.gui.imgui.GenericFlashbackTextField;

@Pseudo
@Mixin(value = ImGui.class, remap = false)
public abstract class FlashbackImGuiMixin {
	
	private static final String preInputTextMethodDescriptor = 
			"preInputText(ZLjava/lang/String;Ljava/lang/String;Limgui/moulberry90/type/ImString;FFILimgui/moulberry90/callback/ImGuiInputTextCallback;)Z";
	
	@Inject(method = preInputTextMethodDescriptor, at = @At("HEAD"))
	private static void captureArgs(boolean multiline, String label, String hint, 
			ImString text, float width, float height, int flagsV, 
			ImGuiInputTextCallback callback, CallbackInfoReturnable<Boolean> cir) {
		GenericFlashbackTextField.getInstance().setMultiline(multiline);
		GenericFlashbackTextField.getInstance().setLabel(label);
	}
	
	@ModifyVariable(method = preInputTextMethodDescriptor, at = @At("HEAD"), ordinal = 0)
	private static int enableCallbacks(int flagsV) {
		flagsV |= ImGuiInputTextFlags.CallbackAlways;
		GenericFlashbackTextField.getInstance().setInputTextFlags(flagsV);
		return flagsV;
	}
	
	@ModifyVariable(method = preInputTextMethodDescriptor, at = @At("HEAD"), ordinal = 0)
	private static ImGuiInputTextCallback attachTextCallback(ImGuiInputTextCallback callback) {
		return GenericFlashbackTextField.getInstance().getFlashbackTextFieldCallback(callback);
	}
}
