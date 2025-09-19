package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(targets = "gg.essential.elementa.UIComponent", remap = false)
public abstract class EssentialUIComponentMixin implements MinecraftFocusableWidget {
	
	@Shadow
	public abstract float getLeft();
	
	@Shadow
	public abstract float getTop();
	
	@Inject(method = "focus", at = @At("TAIL"))
	public void onFocusGained(CallbackInfo ci) {}
	
	@Inject(method = "loseFocus", at = @At("TAIL"))
	public void onFocusLost(CallbackInfo ci) {}

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), (int) getLeft(), (int) getTop(), Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
}
