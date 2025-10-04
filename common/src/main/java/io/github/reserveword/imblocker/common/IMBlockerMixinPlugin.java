package io.github.reserveword.imblocker.common;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.Lists;
import com.sun.jna.Platform;

public class IMBlockerMixinPlugin implements IMixinConfigPlugin {
	
	private static final List<String> validMixins;

	@Override
	public void onLoad(String mixinPackage) {
		
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		
	}

	@Override
	public List<String> getMixins() {
		return validMixins;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}
	
	static {
		validMixins = Lists.newArrayList(
				"AbstractCommandBlockScreenMixin",
				"ChatScreenMixin",
				"AbstractWidgetMixin",
				"MinecraftClientMixin",
				"WindowMixin",
				"UnlockIMEKeyListener",
				"KeyboardHandlerAccessor",
				"TextFieldMixin",
				"AbstractScrollAreaMixin",
				"StringViewAccessor",
				"MultilineTextFieldMixin",
				"MultiLineEditBoxMixin");
		
		if(Platform.isWindows()) {
			validMixins.add("WindowsFullScreenPatch");
		}
		
		if(Platform.isLinux()) {
			validMixins.add("LinuxKeyboardPatch");
		}
		
		if(IMBlockerCore.hasMod("libgui")) {
			validMixins.add("compat.LibGuiWidgetMixin");
			validMixins.add("compat.LibGuiTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("essential")) {
			validMixins.add("compat.EssentialUIComponentMixin");
			validMixins.add("compat.EssentialAbstractTextInputMixin");
		}
	}
}
