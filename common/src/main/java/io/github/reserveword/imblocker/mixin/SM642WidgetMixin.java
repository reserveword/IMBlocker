package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.Widget;

import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = BaseWidget.class, remap = false)
public abstract class SM642WidgetMixin implements MinecraftFocusableWidget {
	
	@Unique
	protected BaseWidget parent;
	
	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Inject(method = "addWidget", at = @At("TAIL"))
	public <T extends Widget> void onWidgetAdded(T widget, CallbackInfoReturnable<T> cir) {
		if(widget instanceof BaseWidget) {
			((SM642WidgetMixin) widget).parent = (BaseWidget) (Object) this;
		}
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		int xAbs = x, yAbs = y;
		SM642WidgetMixin root = this;
		SM642WidgetMixin parent = (SM642WidgetMixin) (Object) this.parent;
		while(parent != null) {
			xAbs += parent.x;
			yAbs += parent.y;
			root = parent;
			parent = (SM642WidgetMixin) (Object) parent.parent;
		}
		
		double renderScale = getGuiScale();
		Rectangle scaledBounds = new Rectangle(renderScale, xAbs, yAbs, width, height);
		Rectangle windowBounds = MinecraftClientAccessor.INSTANCE.getWindowBounds();
		int offsetX = (int) ((windowBounds.width() - (root.width * renderScale)) / 2);
		int offsetY = (int) ((windowBounds.height() - (root.height * renderScale)) / 2);
		return new Rectangle(offsetX + scaledBounds.x(), offsetY + scaledBounds.y(), scaledBounds.width(), scaledBounds.height());
	}
}
