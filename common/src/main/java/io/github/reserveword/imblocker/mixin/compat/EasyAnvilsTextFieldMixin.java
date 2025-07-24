package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Pseudo
@Mixin(targets = {"fuzs.easyanvils.client.gui.components.OpenEditBox", 
		"fuzs.easyanvils.client.gui.components.AdvancedEditBox"}, remap = false)
public abstract class EasyAnvilsTextFieldMixin implements MinecraftTextFieldWidget {
	@Override
	public boolean isRenderable() {
		return true;
	}
}
