package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.client.gui.widget.TextBox;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FtbTextInputWidget;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.minecraft.client.input.CharacterEvent;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin extends FtbWidgetMixin 
	implements MinecraftTextFieldWidget, FtbTextInputWidget {
	
	@Shadow private String text;
	
	@Shadow
	private int displayPos;
	
	@Shadow
	private int cursorPos;
	
	@Shadow
	private boolean isFocused;

	@Unique
	private boolean isRenderable = true;
	
	@Unique
	private long lastRenderTime;
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, height, displayPos, cursorPos, text);
	
	@Override
	public void handleBoundsChanged() {
		imblocker$onBoundsChanged();
	}

	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(this.isFocused);
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(isFocused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}

	@Override
	@Inject(method = "onClosed", at = @At("TAIL"), require = 0)
	public void cancelFocus(CallbackInfo ci) {
		imblocker$onFocusLost();
	}

	@Inject(method = "scrollTo", at = @At("TAIL"))
	public void onCursorPosChanged(int pos, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "isFocused", at = @At("TAIL"))
	public void updateLastRenderTime(CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isGameRendering) {
			lastRenderTime = System.nanoTime();
		}
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, height, displayPos, cursorPos, text);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public void checkVisibility(long lastGameRenderTime) {
		setRenderable(lastRenderTime > lastGameRenderTime);
	}
	
	@Unique
	private void setRenderable(boolean renderable) {
		if(isRenderable != renderable) {
			this.isRenderable = renderable;
			getFocusContainer().locateRealFocus();
		}
	}
	
	@Unique
	@Override
	public boolean isRenderable() {
		return isRenderable;
	}
}
