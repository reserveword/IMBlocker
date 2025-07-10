package io.github.reserveword.imblocker.mixin.compat;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.components.SearchTextFieldComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame", remap = false)
public abstract class RSOAbstractFrameMixin {
	
	@Shadow
	private GuiEventListener focused;

	@SuppressWarnings("unchecked")
	@Overwrite
	public void m_7522_(@Nullable GuiEventListener focused) {
		if (this.focused != focused) {
			if(this.focused != null) {
				if(IMBlockerCore.isGameVersionReached(762/*1.19.4*/)) {
					this.focused.setFocused(false);
				}else if(SearchTextFieldComponent.class.isInstance(this.focused)) {
					ReflectionUtil.invokeMethod(SearchTextFieldComponent.class, this.focused, null, 
							"setFocused", new Class[] {boolean.class}, false);
				}
			}
			
			this.focused = focused;
			Consumer<GuiEventListener> focusListener = ReflectionUtil
					.getFieldValue(getClass(), this, Consumer.class, "focusListener");
			if(focusListener != null) {
				focusListener.accept(focused);
			}
		}
	}
}
