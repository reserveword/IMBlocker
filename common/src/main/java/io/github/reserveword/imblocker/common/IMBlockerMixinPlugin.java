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
				"MultiLineEditBoxMixin",
				"AbstractSignEditScreenMixin",
				"GuiGraphicsExtractorMixin",
				"KeyboardHandlerMixin");
		
		if(Platform.isLinux()) {
			validMixins.add("LinuxKeyboardPatch");
		}
	}
	
	@Override
	public List<String> getMixins() {
		return validMixins;
	}

	public void onLoad(String mixinPackage) {}
	public String getRefMapperConfig() { return null; }
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return true; }
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
