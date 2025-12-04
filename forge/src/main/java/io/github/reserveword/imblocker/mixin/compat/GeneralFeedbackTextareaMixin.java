package io.github.reserveword.imblocker.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.mixin.AbstractWidgetMixin;

//Archived Compatibility for GeneralFeedback 1.0.0
@Mixin(targets = "com.sighs.generalfeedback.client.Textarea", remap = false)
public abstract class GeneralFeedbackTextareaMixin extends AbstractWidgetMixin {
	
	@Shadow
	private int lineHeight;
	
	@Shadow
	private int scrollOffset;
	
	private int imblocker$startLine;
	
	private Point imblocker$caretPos = Point.TOP_LEFT;
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(isFocused());
	}
	
	@Inject(method = "cursorPositionInRendered", at = @At("HEAD"))
	public void updateStartLine(List<String> lines, CallbackInfoReturnable<?> cir) {
		int maxVisibleLines = Math.max(1, (height - 8) / lineHeight);
		int maxStart = Math.max(0, lines.size() - maxVisibleLines);
        imblocker$startLine = Math.min(Math.max(0, scrollOffset), maxStart);
	}
	
	@ModifyArgs(method = "cursorPositionInRendered", at = @At(value = "INVOKE", target = 
			"Lcom/sighs/generalfeedback/client/Textarea$CursorPos;<init>(ILjava/lang/String;)V"))
	public void updateCaretPos(Args args) {
		int lineIndex = args.get(0);
		String textBeforeCursor = args.get(1);
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(textBeforeCursor);
		int caretY = 4 + (lineIndex - imblocker$startLine) * lineHeight;
		Point currentCaretPos = new Point(getGuiScale(), caretX, caretY);
		if(!imblocker$caretPos.equals(currentCaretPos)) {
			imblocker$caretPos = currentCaretPos;
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	public Point getCaretPos() {
		return imblocker$caretPos;
	}
}
