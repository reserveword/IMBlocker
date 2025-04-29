package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(value = WWidget.class, remap = false)
public abstract class CottonWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int width;
	@Shadow protected int height;
    
    @Override
    public boolean isWidgetEditable() {
    	return (getClass().equals(WTextField.class)) && ((WTextField)(Object) this).isEditable();
    }
    
    @Inject(method = "onFocusGained", at = @At("HEAD"))
    public void onFocusGained(CallbackInfo ci) {
    	onFocusGained();
    }
    
    @Inject(method = "onFocusLost", at = @At("HEAD"))
    public void onFocusLost(CallbackInfo ci) {
    	onFocusLost();
    }
    
    @Override
    public Rectangle getBoundsAbs() {
    	return new Rectangle(FocusContainer.getMCGuiScaleFactor(), x, y, width, height);
    }
}
