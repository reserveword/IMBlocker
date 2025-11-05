package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.OffsetGuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;

@Pseudo
@Mixin(targets = "de.johni0702.minecraft.gui.element.AbstractGuiTextField", remap = false)
public abstract class ReplayModTextFieldMixin implements MinecraftTextFieldWidget {
	@Unique private Rectangle bounds;
	
	@Shadow private String text;
	@Shadow private int cursorPos;
	@Shadow private int currentOffset;
	
	@Shadow
	private boolean focused;

	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, 0, currentOffset, cursorPos, text);
	
	@Inject(method = "onFocusChanged", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(focused);
	}
	
	@Inject(method = "typeKey", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(ReadablePoint mousePosition, int keyCode, 
			char keyChar, boolean ctrlDown, boolean shiftDown, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(focused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "draw", at = @At("TAIL"))
	public void updateCaretPos(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo, CallbackInfo ci) {
		if(!isTrulyFocused()) {
			return;
		}
		
		Point position = getPosition(renderer);
		Rectangle currentBounds = new Rectangle(position.x(), position.y(), size.getWidth(), size.getHeight());
		boolean boundsChanged;
		if(boundsChanged = !currentBounds.equals(bounds)) {
			bounds = currentBounds;
		}
		if(updateCursorInfo() || boundsChanged) {
			IMManager.updateCompositionWindowPos();
		}
	}

	private Point getPosition(GuiRenderer renderer) {
		int x = 0, y = 0;
		while(renderer instanceof OffsetGuiRenderer offsetRenderer) {
			ReadablePoint pos = ReflectionUtil.getFieldValue(
					OffsetGuiRenderer.class, offsetRenderer, ReadablePoint.class, "position");
			x += pos.getX();
			y += pos.getY();
			renderer = ReflectionUtil.getFieldValue(
					OffsetGuiRenderer.class, offsetRenderer, GuiRenderer.class, "renderer");
		}
		return new Point(x, y);
	}

	@Unique
	@Override
	public Rectangle getBoundsAbs() {
		return bounds != null ? bounds.derive(getGuiScale()) : Rectangle.EMPTY;
	}

	@Override
	public boolean updateCursorInfo() {
		int height = bounds != null ? bounds.height() : 0;
		return imblocker$cursorInfo.updateCursorInfo(true, height, currentOffset, cursorPos, text);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
}
