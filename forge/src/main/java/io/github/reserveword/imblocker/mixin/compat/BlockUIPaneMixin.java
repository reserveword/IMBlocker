package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingContainer;
import com.ldtteam.blockui.views.View;

import io.github.reserveword.imblocker.common.gui.MathHelper;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = Pane.class, remap = false)
public abstract class BlockUIPaneMixin implements MinecraftFocusableWidget {
	
	@Shadow
	protected View parent;
	
	@Shadow
	protected BOWindow window;

	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Inject(method = "onFocusLost", at = @At("TAIL"))
	public void focusLost(CallbackInfo ci) {}
	
	@Override
	public Rectangle getBoundsAbs() {
		int xAbs = x, yAbs = y;
		
		for(View parent = this.parent; parent != null; parent = ((BlockUIPaneMixin) (Object) parent).parent) {
			if(ScrollingContainer.class.isInstance(parent)) {
				yAbs -= ((ScrollingContainer) (Object) parent).getScrollY();
				yAbs = MathHelper.clamp(yAbs, 0, parent.getHeight() - 4);
			}
			xAbs += parent.getX() + ((BlockUIViewAccessor) parent).getPadding();
			yAbs += parent.getY() + ((BlockUIViewAccessor) parent).getPadding();
		}
		
		double renderScale = window.getScreen().getRenderScale();
		xAbs = (int) (xAbs * renderScale + ((BlockUIBOScreenAccessor) window.getScreen()).getX());
		yAbs = (int) (yAbs * renderScale + ((BlockUIBOScreenAccessor) window.getScreen()).getY());
		
		return new Rectangle(xAbs, yAbs, (int) (width * renderScale), (int) (height * renderScale));
	}
	
	@Override
	public double getGuiScale() {
		return window.getScreen().getRenderScale();
	}
}
