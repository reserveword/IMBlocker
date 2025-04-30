package io.github.reserveword.imblocker.mixin.neoforge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = Widget.class, remap = false)
public abstract class FtbWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow public abstract int getX();
	@Shadow public abstract int getY();
	@Shadow public int width;
	@Shadow public int height;

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), getX(), getY(), width, height);
	}
}
