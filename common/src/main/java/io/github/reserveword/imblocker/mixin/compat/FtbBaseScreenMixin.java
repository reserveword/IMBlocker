package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FtbTextInputWidget;

@Mixin(BaseScreen.class)
public abstract class FtbBaseScreenMixin extends FtbWidgetMixin {
	@Override
	public void handleWidthChanged(int width, CallbackInfo ci) {
		posX = (MinecraftClientAccessor.INSTANCE.getGuiScaledWidth() - this.width) / 2;
		if(FocusManager.getFocusOwner() instanceof FtbTextInputWidget) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	public void handleHeightChanged(int height, CallbackInfo ci) {
		posY = (MinecraftClientAccessor.INSTANCE.getGuiScaledHeight() - this.height) / 2;
		if(FocusManager.getFocusOwner() instanceof FtbTextInputWidget) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	public void handleBoundsChanged() {
		if(FocusManager.getFocusOwner() instanceof FtbTextInputWidget) {
			IMManager.updateCompositionWindowPos();
		}
	}
}
