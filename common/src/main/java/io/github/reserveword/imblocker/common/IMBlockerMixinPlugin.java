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
				"GuiMixin",
				"UnlockIMEKeyListener",
				"KeyboardHandlerAccessor",
				"TextFieldMixin",
				"AbstractScrollAreaMixin",
				"StringViewAccessor",
				"MultilineTextFieldMixin",
				"MultiLineEditBoxMixin",
				"AbstractSignEditScreenMixin",
				"GuiGraphicsExtractorMixin",
				"TextInputManagerMixin",
				"KeyboardHandlerMixin");
		
		if(Platform.isWindows()) {
			validMixins.add("WindowsIngameIMEInitializer");
		}
		
		if(MinecraftClientUtil.isGameVersionReached(777/*26.3*/)) {
			validMixins.add("SDLIMEHintTweaker");
			if(!Platform.isWindows()) {
				validMixins.add("SDLKey2CharPatch");
			}
		}
		
		if(!IMBlockerCore.IS_SDL_PRESENT && Platform.isLinux()) {
			validMixins.add("LinuxKeyboardPatch");
		}
		
		if(IMBlockerCore.hasMod("axiom")) {
			validMixins.add("compat.AxiomEditorUIMixin");
			validMixins.add("compat.ImGuiMixin");
			validMixins.add("compat.ImGuiGlfwPatch");
		}
		
		if(IMBlockerCore.hasMod("ftblibrary")) {
			validMixins.add("compat.FtbWidgetMixin");
			validMixins.add("compat.FtbTextFieldMixin");
			validMixins.add("compat.FtbPanelMixin");
			validMixins.add("compat.FtbBaseScreenMixin");
			validMixins.add("compat.FtbMultilineTextBoxMixin");
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
			validMixins.add("compat.RSOBaseWidgetMixin");
			validMixins.add("compat.RSOTextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("supermartijn642corelib")) {
			validMixins.add("compat.SM642WidgetMixin");
			validMixins.add("compat.SM642TextFieldMixin");
		}
		
		if(IMBlockerCore.hasMod("essential")) {
			validMixins.add("compat.EssentialUIComponentMixin");
			validMixins.add("compat.EssentialAbstractTextInputMixin");
		}
		
		if(IMBlockerCore.hasMod("armourers_workshop")) {
			validMixins.add("compat.AWTextInputWidgetMixin");
		}

		if(IMBlockerCore.hasMod("modernui")) {
			validMixins.add("compat.ModernUISelectionMixin");
			validMixins.add("compat.ModernUITextViewMixin");
			validMixins.add("compat.ModernUIEditTextMixin");
		}
		
		if(IMBlockerCore.hasMod("ldlib2")) {
			validMixins.add("compat.LDLibUIElementMixin");
			validMixins.add("compat.LDLibTextFieldMixin");
			validMixins.add("compat.LDLibTextAreaMixin");
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
