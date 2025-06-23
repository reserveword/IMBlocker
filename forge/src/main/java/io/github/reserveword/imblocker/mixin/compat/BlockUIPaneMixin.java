package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
	
	@Unique
	private BlockUIBOScreenMixin getBOScreen() {
		return (BlockUIBOScreenMixin) (Object) window.getScreen();
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		int xAbs = x, yAbs = y;
		
		for(BlockUIViewMixin parent = (BlockUIViewMixin) (Object) this.parent;
				parent != null; parent = (BlockUIViewMixin) (Object) parent.parent) {
			if(ScrollingContainer.class.isInstance(parent)) {
				yAbs -= ((ScrollingContainer) (Object) parent).getScrollY();
				yAbs = MathHelper.clamp(yAbs, 0, parent.height - 4);
			}
			xAbs += parent.x + parent.padding;
			yAbs += parent.y + parent.padding;
		}
		
		BlockUIBOScreenMixin screen = getBOScreen();
		double renderScale = screen.renderScale;
		xAbs = (int) (xAbs * renderScale + screen.x);
		yAbs = (int) (yAbs * renderScale + screen.y);
		
		return new Rectangle(xAbs, yAbs, (int) (width * renderScale), (int) (height * renderScale));
	}
	
	@Override
	public double getGuiScale() {
		return getBOScreen().renderScale;
	}
}
