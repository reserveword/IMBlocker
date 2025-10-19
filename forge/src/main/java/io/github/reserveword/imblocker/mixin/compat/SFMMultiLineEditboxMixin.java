package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.mixin.MultiLineEditBoxMixin;

@Pseudo
@Mixin(targets = "ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1$MyMultiLineEditBox", remap = false)
public abstract class SFMMultiLineEditboxMixin extends MultiLineEditBoxMixin {
	
	@Shadow
	public abstract int getLineNumberWidth();
	
	@Inject(method = "onValueOrCursorChanged", at = @At("TAIL"))
	public void onCursorChange(String programString, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean getPreferredEnglishState() {
		return true;
	}
	
	@Override
	public int imblocker$getContentOffsetX() {
		return 4 + getLineNumberWidth();
	}
}
