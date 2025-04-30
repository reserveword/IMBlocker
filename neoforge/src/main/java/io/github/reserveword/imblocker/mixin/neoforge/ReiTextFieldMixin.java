package io.github.reserveword.imblocker.mixin.neoforge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"})
public abstract class ReiTextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean editable;

    @Shadow
    private me.shedaniel.math.Rectangle bounds;
	
	@Override
	public boolean isWidgetEditable() {
		return editable;
	}
	
	@Inject(method = {"setFocused", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onFocusChanged(isFocused);
	}
	
	@Inject(method = {"setEditable", "setIsEditable"}, at = @At("HEAD"))
    public void setIsEditable(boolean editable, CallbackInfo ci) {
		if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
    }

	@Override
    public Rectangle getBoundsAbs() {
    	return new Rectangle(FocusContainer.getMCGuiScaleFactor(), 
    			bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
