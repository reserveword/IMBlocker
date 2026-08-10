package io.github.reserveword.imblocker.mixin.compat;

import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lowdragmc.lowdraglib2.client.font.LDFonts;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

@Mixin(value = UIElement.class, remap = false)
public abstract class LDLibUIElementMixin implements MinecraftFocusableWidget {
	@Shadow
	private UIElement parent;
	
	@Shadow public abstract float getContentX();
	@Shadow public abstract float getContentY();
	@Shadow public abstract float getContentWidth();
	@Shadow public abstract float getContentHeight();
	
	private final Matrix3x2f imblocker$identityPose = new Matrix3x2f();
	protected Matrix3x2f imblocker$currentPose = new Matrix3x2f();
	
	@Inject(method = "addClass", at = @At("TAIL"))
	public void onClassAdded(String clazz, CallbackInfoReturnable<?> cir) {
		if(clazz.equals("__focused__") || clazz.equals("__disabled__")) {
			imblocker$onFocusFactorsChanged();
		}
	}
	
	@Inject(method = "removeClass", at = @At("TAIL"))
	public void onClassRemoved(String clazz, CallbackInfoReturnable<?> cir) {
		if(clazz.equals("__focused__") || clazz.equals("__disabled__")) {
			imblocker$onFocusFactorsChanged();
		}
	}
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void onVisibilityChanged(boolean isVisible, CallbackInfoReturnable<?> cir) {
		imblocker$onFocusFactorsChanged();
	}
	
	@Inject(method = "markTaffyStyleDirty", at = @At("TAIL"))
	public void onTaffyStyleChanged(CallbackInfo ci) {
		imblocker$onFocusFactorsChanged();
	}
	
	@Inject(method = "drawInBackgroundInternal", at = @At("HEAD"))
	public void updateTransform2D(IGUIContext context, CallbackInfo ci) {
		Matrix3x2f currentPos = context.currentPose();
		if(!currentPos.equals(imblocker$currentPose)) {
			imblocker$currentPose = new Matrix3x2f(currentPos);
		}
	}
	
	public void imblocker$onFocusFactorsChanged() {}
	
	protected int imblocker$getStringWidth(String text, Identifier font) {
		FontDescription.Resource fontResource = new FontDescription.Resource(font);
		Font fontRenderer;
		try {
			fontRenderer = LDFonts.font();
		} catch (Throwable e) {
			fontRenderer = Minecraft.getInstance().font;
		}
		return fontRenderer.width(Component.literal(text).withStyle(style -> style.withFont(fontResource)));
	}
	
	protected boolean imblocker$isPoseIdentity() {
		return imblocker$identityPose.equals(imblocker$currentPose);
	}
	
	protected UIElement imblocker$getAnchorWidget() {
		return (UIElement) (Object) this;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		UIElement anchor = imblocker$getAnchorWidget();
		float x = anchor.getContentX(), y = anchor.getContentY(), 
				width = anchor.getContentWidth(), height = anchor.getContentHeight();
		if(!imblocker$isPoseIdentity()) {
			Matrix3x2f t = imblocker$currentPose;
			// assert no rotation/shear.
			x = t.m00 * x + t.m20;
			y = t.m11 * y + t.m21;
			width *= t.m00;
			height *= t.m11;
		}
		return new Rectangle(getGuiScale(), x, y, width, height);
	}
}
