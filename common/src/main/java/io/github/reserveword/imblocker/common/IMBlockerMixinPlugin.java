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
				"MultiLineEditBoxMixin");
		
		if(Platform.isWindows()) {
			validMixins.add("WindowsFullScreenPatch");
		}
		
		if(Platform.isLinux()) {
			validMixins.add("LinuxKeyboardPatch");
		}
		
		if(IMBlockerCore.hasMod("axiom")) {
			validMixins.add("compat.AxiomEditorUIMixin");
			validMixins.add("compat.ImGuiMixin");
		}
		
		if(IMBlockerCore.hasMod("libgui")) {
			validMixins.add("compat.LibGuiWidgetMixin");
			validMixins.add("compat.LibGuiTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("roughlyenoughitems")) {
			validMixins.add("compat.ReiTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("replaymod")) {
			validMixins.add("compat.ReplayModTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("meteor-client")) {
			validMixins.add("compat.MeteorWidgetMixin");
			validMixins.add("compat.MeteorTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("reeses-sodium-options") || 
				IMBlockerCore.hasMod("reeses_sodium_options")) {
			validMixins.add("compat.RSOAbstractFrameMixin");
			validMixins.add("compat.RSOSearchFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("notes")) {
			validMixins.add("compat.NotesTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("essential")) {
			validMixins.add("compat.EssentialUIComponentMixin");
			validMixins.add("compat.EssentialAbstractTextInputMixin");
		}
		
		if(IMBlockerCore.hasMod("armourers_workshop")) {
			validMixins.add("compat.AWTextInputWidgetMixin");
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
