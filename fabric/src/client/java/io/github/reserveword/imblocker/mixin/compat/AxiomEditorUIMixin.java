package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import imgui.ImGuiIO;
import io.github.reserveword.imblocker.AxiomGuiMonitor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Pseudo
@Mixin(targets = "com.moulberry.axiom.editor.EditorUI", remap = false)
public abstract class AxiomEditorUIMixin {
	
	@Shadow
	public static ImGuiIO imGuiIO;
	
	@Inject(method = "init", at = @At("TAIL"))
	private static void loadImGui(CallbackInfo ci) {
		ClientTickEvents.START_CLIENT_TICK.register(new AxiomGuiMonitor(imGuiIO));
	}
}
