package io.github.reserveword.imblocker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IMBlockerMixinPlugin implements IMixinConfigPlugin {
	
	private static final List<String> validMixins = new ArrayList<>();

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
		if(IMBlocker.isGameVersionReached(762/*1.19.4*/)) {
			validMixins.add("AbstractParentElementMixin");
			validMixins.add("TextFieldMixin");
		}else {
			validMixins.add("TextFieldLegacyMixin");
		}

		if(IMBlocker.isGameVersionReached(760/*1.19.1*/)) {
			validMixins.add("ScrollableWidgetMixin");
			validMixins.add("SubstringAccessor");
			validMixins.add("EditBoxAccessor");
			validMixins.add("EditBoxMixin");
			validMixins.add("EditBoxWidgetMixin");
		}
		
		if(IMBlocker.hasMod("axiom")) {
			validMixins.add("AxiomEditorUIMixin");
		}
		
		if(IMBlocker.hasMod("ftblibrary")) {
			validMixins.add("FtbWidgetMixin");
			validMixins.add("FtbBaseScreenMixin");
			
			if(IMBlocker.isGameVersionReached(763/*1.20*/)) {
				validMixins.add("FtbTextFieldMixin");
			}else {
				validMixins.add("FtbTextFieldLegacyMixin");
			}
			
			if(IMBlocker.isGameVersionReached(760/*1.19.1*/)) {
				validMixins.add("FtbMultilineTextBoxMixin");
			}
		}
		
		if(IMBlocker.hasMod("libgui")) {
			validMixins.add("LibGuiWidgetMixin");
			validMixins.add("LibGuiTextFieldMixin");
		}
		
		if(IMBlocker.hasMod("emi")) {
			if(IMBlocker.isGameVersionReached(762/*1.19.4*/)) {
				validMixins.add("EmiSearchWidgetMixin");
			}else {
				validMixins.add("EmiSearchWidgetLegacyMixin");
			}
		}
		
		if(IMBlocker.hasMod("roughlyenoughitems")) {
			validMixins.add("ReiTextFieldMixin");
		}
		
		if(IMBlocker.hasMod("replaymod")) {
			validMixins.add("ReplayModTextFieldMixin");
		}
		
		if(IMBlocker.hasMod("meteor-client")) {
			validMixins.add("MeteorWidgetMixin");
			validMixins.add("MeteorTextFieldMixin");
		}
		
		if(IMBlocker.hasMod("reeses-sodium-options")) {
			validMixins.add("RSOAbstractFrameMixin");
			validMixins.add("SodiumSearchFieldMixin");
		}
	}
}
