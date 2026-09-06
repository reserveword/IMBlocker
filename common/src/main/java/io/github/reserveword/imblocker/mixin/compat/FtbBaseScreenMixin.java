package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.client.gui.widget.BaseScreen;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FtbTextInputWidget;
import net.minecraft.client.Minecraft;

@Mixin(BaseScreen.class)
public abstract class FtbBaseScreenMixin extends FtbPanelMixin {
	@Override
	public void handleWidthChanged(int width, CallbackInfo ci) {
		posX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - this.width) / 2;
		if(FocusManager.getFocusOwner() instanceof FtbTextInputWidget) {
			IMManager.updateCaretPosition();
		}
	}
	
	@Override
	public void handleHeightChanged(int height, CallbackInfo ci) {
		posY = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - this.height) / 2;
		if(FocusManager.getFocusOwner() instanceof FtbTextInputWidget) {
			IMManager.updateCaretPosition();
		}
	}
}
