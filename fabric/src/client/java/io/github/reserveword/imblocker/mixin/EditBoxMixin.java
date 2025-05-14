package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.ReflectionUtil;
import net.minecraft.client.gui.EditBox;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
	
	@Shadow
	private List<SubstringAccessor> lines;
	
	@Inject(method = "rewrap", at = @At("TAIL"))
	public void amendLines(CallbackInfo ci) {
		for(int i = lines.size() - 1; i > 0; i--) {
			SubstringAccessor currentLine = lines.get(i);
			SubstringAccessor previousLine = lines.get(i - 1);
			if(previousLine.getEndIndex() >= currentLine.getBeginIndex()) {
				lines.set(i - 1, ReflectionUtil.newInstance(previousLine.getClass(), 
						new Class<?>[] {int.class, int.class}, 
						previousLine.getBeginIndex(), currentLine.getBeginIndex() - 1));
			}
		}
	}
}
