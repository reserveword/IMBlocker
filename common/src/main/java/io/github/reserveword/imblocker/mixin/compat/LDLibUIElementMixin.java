package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = UIElement.class, remap = false)
public abstract class LDLibUIElementMixin implements MinecraftFocusableWidget {
	@Shadow public abstract float getContentX();
	@Shadow public abstract float getContentY();
	@Shadow public abstract float getContentWidth();
	@Shadow public abstract float getContentHeight();
	
	@Inject(method = "addClass", at = @At("TAIL"))
	public void onClassAdded(String clazz, CallbackInfoReturnable<?> cir) {
		if(clazz.equals("__focused__") || clazz.equals("__disabled__")) {
			imblocker$onFocusFactorsChanged();
		}
	}
	
	@Inject(method = "removeClass", at = @At("TAIL"))
	public void onClassRemoved(String clazz, CallbackInfoReturnable<?> cir) {
		if(clazz.equals("__focused__") || clazz.equals("__disabled__")) {
			imblocker$onFocusFactorsChanged();
		}
	}
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void onVisibilityChanged(boolean isVisible, CallbackInfoReturnable<?> cir) {
		imblocker$onFocusFactorsChanged();
	}
	
	@Inject(method = "markTaffyStyleDirty", at = @At("TAIL"))
	public void onTaffyStyleChanged(CallbackInfo ci) {
		imblocker$onFocusFactorsChanged();
	}
	
	public void imblocker$onFocusFactorsChanged() {}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), (int) getContentX(), (int) getContentY(), (int) getContentWidth(), (int) getContentHeight());
	}
}
