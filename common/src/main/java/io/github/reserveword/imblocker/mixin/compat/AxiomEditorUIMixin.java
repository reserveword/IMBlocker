package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.AxiomGuiMonitor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;

@Pseudo
@Mixin(targets = "com.moulberry.axiom.editor.EditorUI", remap = false)
public abstract class AxiomEditorUIMixin {
	@Inject(method = "init", at = @At("TAIL"))
	private static void loadImGui(CallbackInfo ci) {
		AxiomGuiMonitor.createInstance();
		IMBlockerCore.registerClientTickEvent(AxiomGuiMonitor.getInstance()::tick);
	}
	
	@Inject(method = "drawOverlayInternal", at = @At(value = "INVOKE", target = "Limgui/moulberry92/ImGui;render()V"))
	private static void renderUniversalPreeditOverlay(CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.IMGUI) {
			ImDrawList graphics = ImGui.getForegroundDrawList();
			UniversalIMEPreeditOverlay.getInstance().renderOnImGuiSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnImGuiSurface(graphics);
			UniversalEnglishStateIndicator.renderOnImGuiSurface(graphics);
		}
	}
}
