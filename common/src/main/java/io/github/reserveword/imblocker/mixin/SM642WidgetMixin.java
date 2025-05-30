package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.supermartijn642.core.gui.widget.BaseWidget;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = BaseWidget.class, remap = false)
public abstract class SM642WidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), x, y, width, height);
	}
}
