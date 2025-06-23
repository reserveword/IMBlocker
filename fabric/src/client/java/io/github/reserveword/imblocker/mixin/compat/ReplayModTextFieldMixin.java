package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.OffsetGuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.RenderInfo;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = "com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField", remap = false)
public abstract class ReplayModTextFieldMixin implements MinecraftTextFieldWidget {
	
	private Rectangle bounds;
	private int lastCursorPos;
	private int lastOffset;
	
	@Shadow private String text;
	@Shadow private int cursorPos;
	@Shadow private int currentOffset;

	@Inject(method = "onFocusChanged", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}

	@Inject(method = "draw", at = @At("TAIL"))
	public void updateCaretPos(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo, CallbackInfo ci) {
		Point position = getPosition(renderer);
		Rectangle currentBounds = new Rectangle(position.x(), position.y(), size.getWidth(), size.getHeight());
		if(!currentBounds.equals(bounds) || (lastCursorPos != cursorPos) || (lastOffset != currentOffset)) {
			bounds = currentBounds;
			lastCursorPos = cursorPos;
			lastOffset = currentOffset;
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

	@Override
	public Rectangle getBoundsAbs() {
		return bounds != null ? bounds.derive(getGuiScale()) : MinecraftTextFieldWidget.super.getBoundsAbs();
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return bounds != null ? new SinglelineCursorInfo(true, bounds.height(), currentOffset, cursorPos, text) : null;
	}
}
