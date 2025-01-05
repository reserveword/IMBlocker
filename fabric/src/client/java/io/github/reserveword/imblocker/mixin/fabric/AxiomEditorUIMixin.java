package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.AxiomGuiAccessor;

import imgui.ImGuiIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.moulberry.axiom.editor.EditorUI", remap = false)
public abstract class AxiomEditorUIMixin {
    @Shadow
    public static ImGuiIO imGuiIO;

    @Inject(method = "init", at = @At("TAIL"))
    private static void loadImGui(CallbackInfo ci) {
        AxiomGuiAccessor.instance = new AxiomGuiAccessor() {
            @Override
            public boolean isTextFieldFocused() {
                return imGuiIO.getWantTextInput();
            }

            @Override
            public boolean isCaptureKeyboard() {
                return imGuiIO.getWantCaptureKeyboard();
            }
        };
    }
}
