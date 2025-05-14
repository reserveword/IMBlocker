package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.ReflectionUtil;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldMixin {

	@Shadow
	List<StringViewAccessor> displayLines;
	
	@Inject(method = "reflowDisplayLines", at = @At("TAIL"))
	public void amendLines(CallbackInfo ci) {
		for(int i = displayLines.size() - 1; i > 0; i--) {
			StringViewAccessor currentLine = displayLines.get(i);
			StringViewAccessor previousLine = displayLines.get(i - 1);
			if(previousLine.getEndIndex() >= currentLine.getBeginIndex()) {
				displayLines.set(i - 1, ReflectionUtil.newInstance(previousLine.getClass(),
						new Class<?>[] {int.class, int.class},
						previousLine.getBeginIndex(), currentLine.getBeginIndex() - 1));
			}
		}
	}
}
