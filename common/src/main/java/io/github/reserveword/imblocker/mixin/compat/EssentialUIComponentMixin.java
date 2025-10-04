package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = "gg.essential.elementa.UIComponent", remap = false)
public abstract class EssentialUIComponentMixin implements MinecraftFocusableWidget {
	
	@Shadow
	public abstract float getLeft();
	
	@Shadow
	public abstract float getTop();
	
	@Shadow
	public abstract float getWidth();
	
	@Shadow
	public abstract float getHeight();
	
	@Shadow
	public abstract float getTextScale();
	
	@Inject(method = "focus", at = @At("TAIL"))
	public void onFocusGained(CallbackInfo ci) {}
	
	@Inject(method = "loseFocus", at = @At("TAIL"))
	public void onFocusLost(CallbackInfo ci) {}
	
	@Inject(method = "keyType", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char c, int keyCode, CallbackInfo ci) {}

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), (int) getLeft(), (int) getTop(), (int) getWidth(), (int) getHeight());
	}
}
