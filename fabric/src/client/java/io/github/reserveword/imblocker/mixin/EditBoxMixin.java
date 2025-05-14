package io.github.reserveword.imblocker.mixin;

import java.lang.reflect.Constructor;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.EditBox;

@Mixin(EditBox.class)
public class EditBoxMixin {
	
	@Shadow
	private List<SubstringAccessor> lines;
	
	@Inject(method = "rewrap", at = @At("TAIL"))
	public void amendLines(CallbackInfo ci) {
		for(int i = lines.size() - 1; i > 0; i--) {
			SubstringAccessor currentLine = lines.get(i);
			SubstringAccessor previousLine = lines.get(i - 1);
			if(previousLine.getEndIndex() >= currentLine.getBeginIndex()) {
				try {
					SubstringAccessor amendedLine;
					Constructor<? extends SubstringAccessor> constructor = previousLine
							.getClass().getDeclaredConstructor(int.class, int.class);
					constructor.setAccessible(true);
					amendedLine = constructor.newInstance(previousLine.getBeginIndex(), currentLine.getBeginIndex() - 1);
					lines.set(i - 1, amendedLine);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
