package io.github.reserveword.imblocker.mixin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin implements MinecraftFocusableWidget {
	private static final MethodHandle getSignTextScale_Method;
	
	@Shadow protected SignBlockEntity sign;
	@Shadow private String[] messages;
	@Shadow private TextFieldHelper signField;
	@Shadow private int line;
	
	private int imblocker$cursorPos = -1;
	private int imblocker$lineIndex = -1;
	
	@Shadow
	protected abstract float getSignYOffset();
	
	@Inject(method = "init", at = @At("TAIL"))
	public void requestFocus(CallbackInfo ci) {
		imblocker$onFocusGained();
		imblocker$updateCaretPos();
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			FocusContainer.MINECRAFT.switchFocus(this);
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "charTyped", at = @At("RETURN"))
	public void checkCursorPos(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
		imblocker$updateCaretPos();
	}
	
	@Inject(method = "keyPressed", at = @At("RETURN"))
	public void checkCursorPos(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		imblocker$updateCaretPos();
	}
	
	private void imblocker$updateCaretPos() {
		boolean caretChanged = false;
		int cursorPos = signField.getCursorPos();
		if(imblocker$cursorPos != cursorPos) {
			imblocker$cursorPos = cursorPos;
			caretChanged = true;
		}
		if(imblocker$lineIndex != line) {
			imblocker$lineIndex = line;
			caretChanged = true;
		}
		if(caretChanged) {
			IMManager.updateCaretPosition();
		}
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return getFocusContainer().getBoundsAbs();
	}
	
	@Override
	public Point getCaretPos() {
		Font font = ((Screen) (Object) this).getFont();
		String lineContent = messages[line];
		if(lineContent != null) {
			if(font.isBidirectional()) {
				lineContent = font.bidirectionalShaping(lineContent);
			}
		}else {
			return Point.TOP_LEFT;
		}

		float offsetX = ((Screen) (Object) this).width / 2.0F;
		float offsetY = getSignYOffset();
		float cursorX = font.width(lineContent.substring(
				0, Math.max(Math.min(imblocker$cursorPos, lineContent.length()), 0))) - font.width(lineContent) / 2;
		float cursorY = (line - 2) * sign.getTextLineHeight();
		Vector3fc signTextScale = imblocker$getSignTextScale();
		return new Point(getGuiScale(), 
				(int) (offsetX + cursorX * signTextScale.x()), 
				(int) (offsetY + cursorY * signTextScale.y()));
	}
	
	private Vector3fc imblocker$getSignTextScale() {
		try {
			return (Vector3fc) getSignTextScale_Method.invoke(this);
		} catch (Throwable e) {
			System.out.println(e);
			return null;
		}
	}
	
	static {
		MethodHandle getSignTextScale = null;
		try {
			getSignTextScale = MethodHandles.lookup().findVirtual(AbstractSignEditScreen.class, "getSignTextScale", MethodType.methodType(Vector3f.class));
		} catch (Throwable e) {
			try {
				getSignTextScale = MethodHandles.lookup().findVirtual(AbstractSignEditScreen.class, "getSignTextScale", MethodType.methodType(Vector3fc.class));
			} catch (Throwable e2) {}
		}
		getSignTextScale_Method = getSignTextScale;
	}
}
