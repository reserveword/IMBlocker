package io.github.reserveword.imblocker.mixin.compat.imgui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moulberry.axiom.editor.EditorUI;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.accessor.ImGuiGraphicsAccessor;
import io.github.reserveword.imblocker.common.accessor.ImGuiIOAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.imgui.ImGuiManager;

@Pseudo
@Mixin(targets = "com.moulberry.axiom.editor.EditorUI", remap = false)
public abstract class AxiomEditorUIMixin {
	
	@Shadow
	private static boolean isActiveInternal() { throw new AssertionError(); }
	
	@Inject(method = "init", at = @At("TAIL"))
	private static void loadImGui(CallbackInfo ci) {
		ImGuiManager.registerImGuiIO(new ImGuiIOAccessor() {
			@Override
			public boolean isTextFieldFocused() {
				return EditorUI.imGuiIO.getWantCaptureKeyboard();
			}
			
			@Override
			public boolean isCaptureKeyboard() {
				return EditorUI.imGuiIO.getWantTextInput();
			}
		});
	}
	
	@Inject(method = "drawOverlayInternal", at = @At(value = "INVOKE", target = "Limgui/moulberry92/ImGui;render()V"))
	private static void renderUniversalIMEOverlays(CallbackInfo ci) {
		ImDrawList graphics = ImGui.getForegroundDrawList();
		ImGuiManager.drawIMEOverlays(new ImGuiGraphicsAccessor() {
			@Override
			public void addText(float posX, float posY, int col, String textBegin) {
				graphics.addText(posX, posY, col, textBegin);
			}
			
			@Override
			public void addRectFilled(float pMinX, float pMinY, float pMaxX, float pMaxY, int col) {
				graphics.addRectFilled(pMinX, pMinY, pMaxX, pMaxY, col);
			}

			@Override
			public float getTextWidth(String text) {
				return ImGui.calcTextSize(text).x;
			}

			@Override
			public int getColorU32i(int color) {
				return ImGui.getColorU32i(color);
			}
			
			@Override
			public int getColorU32(float a, float r, float g, float b) {
				return ImGui.getColorU32(a, r, g, b);
			}
		});
	}
	
	@Inject(method = "updateWindowSize", at = @At("TAIL"))
	private static void updateGameContentOffset(CallbackInfo ci) {
		if(isActiveInternal()) {
			FocusContainer.MINECRAFT.setGameContentOffset(EditorUI.frameX, EditorUI.frameY);
		}else {
			FocusContainer.MINECRAFT.setGameContentOffset(0, 0);
		}
	}
}
