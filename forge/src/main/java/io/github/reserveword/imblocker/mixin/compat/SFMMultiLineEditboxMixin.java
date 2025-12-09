package io.github.reserveword.imblocker.mixin.compat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditorUtils;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.mixin.MultiLineEditBoxMixin;
import net.minecraft.client.Minecraft;

@Pseudo
@Mixin(targets = "ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1$MyMultiLineEditBox", remap = false)
public abstract class SFMMultiLineEditboxMixin extends MultiLineEditBoxMixin {
	private static final MethodHandle getLineNumberWidth_Method;

	public int imblocker$getLineNumberWidth() {
		if(getLineNumberWidth_Method != null) {
			// <=4.26.0
			try {
				return (int) getLineNumberWidth_Method.invokeExact(this);
			} catch (Throwable e) {
				return 0;
			}
		}else {
			try {
				// >=4.27.0
				return SFMTextEditorUtils.getLineNumberWidth(Minecraft.getInstance().font, imblocker$getTextField().getLineCount());
			} catch (Throwable e) {
				// Fallback
				if(IMBlockerConfig.INSTANCE.sfm$showLineNumber()) {
					return MinecraftClientAccessor.INSTANCE.getStringWidth("0".
							repeat(String.valueOf(imblocker$getTextField().getLineCount()).length()));
				}else {
					return 0;
				}
			}
		}
	}
	
	@Inject(method = "onValueOrCursorChanged", at = @At("TAIL"))
	public void onCursorChange(String programString, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean getPreferredEnglishState() {
		return true;
	}
	
	@Override
	public int imblocker$getContentOffsetX() {
		return 4 + imblocker$getLineNumberWidth();
	}
	
	static {
		MethodHandle getLineNumberWidth = null;
		try {
			Class<?> targetClass = Class.forName("ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1$MyMultiLineEditBox");
			getLineNumberWidth = MethodHandles.lookup().findVirtual(targetClass, "getLineNumberWidth", MethodType.methodType(int.class));
		} catch (Throwable e) {}
		getLineNumberWidth_Method = getLineNumberWidth;
	}
}
