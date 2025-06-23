package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor.Mapping;

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
		boolean isOfficialMapping = Common.getMapping() == Mapping.OFFICIAL;
		
		validMixins.add("AbstractCommandBlockScreenMixin");
		validMixins.add("ChatScreenMixin");
		validMixins.add(isOfficialMapping ? "AbstractWidgetMixin" : "ClickableWidgetMixin");
		validMixins.add("MinecraftClientMixin");
		validMixins.add("WindowMixin");
		
		if(Common.isGameVersionReached(762/*1.19.4*/)) {
			validMixins.add(isOfficialMapping ? "AbstractContainerEventHandlerMixin" : "AbstractParentElementMixin");
			validMixins.add("TextFieldMixin");
		}else {
			validMixins.add("TextFieldLegacyMixin");
		}
		
		if(Common.isGameVersionReached(760/*1.19.1*/)) {
			if(isOfficialMapping) {
				validMixins.add("AbstractScrollWidgetMixin");
				validMixins.add("StringViewAccessor");
				validMixins.add("MultilineTextFieldMixin");
				validMixins.add("MultiLineEditBoxMixin");
			}else {
				validMixins.add("ScrollableWidgetMixin");
				validMixins.add("SubstringAccessor");
				validMixins.add("EditBoxMixin");
				validMixins.add("EditBoxWidgetMixin");
			}
		}
		
		if(Common.hasMod("axiom")) {
			validMixins.add("compat.AxiomEditorUIMixin");
		}
		
		if(Common.hasMod("ftblibrary")) {
			validMixins.add("compat.FtbWidgetMixin");
			
			if(Common.isGameVersionReached(763/*1.20*/)) {
				validMixins.add("compat.FtbTextFieldMixin");
			}else {
				validMixins.add("compat.FtbTextFieldLegacyMixin");
			}
			
			if(Common.isGameVersionReached(760/*1.19.1*/)) {
				validMixins.add("compat.FtbPanelMixin");
				validMixins.add("compat.FtbMultilineTextFieldAccessorImpl");
				validMixins.add("compat.FtbMultilineTextBoxMixin");
			}
		}
		
		if(Common.hasMod("libgui")) {
			validMixins.add("compat.LibGuiWidgetMixin");
			validMixins.add("compat.LibGuiTextFieldMixin");
		}
		
		if(Common.hasMod("emi")) {
			if(Common.isGameVersionReached(762/*1.19.4*/)) {
				validMixins.add("compat.EmiSearchWidgetMixin");
			}else {
				validMixins.add("compat.EmiSearchWidgetLegacyMixin");
			}
		}
		
		if(Common.hasMod("roughlyenoughitems")) {
			validMixins.add("compat.ReiTextFieldMixin");
		}
		
		if(Common.hasMod("replaymod")) {
			validMixins.add("compat.ReplayModTextFieldMixin");
		}
		
		if(Common.hasMod("meteor-client")) {
			validMixins.add("compat.MeteorWidgetMixin");
			validMixins.add("compat.MeteorTextFieldMixin");
		}
		
		if(Common.hasMod("reeses-sodium-options") || Common.hasMod("reeses_sodium_options")) {
			validMixins.add("compat.RSOAbstractFrameMixin");
			validMixins.add("compat.RSOSearchFieldMixin");
		}
		
		if(Common.hasMod("blockui")) {
			validMixins.add("compat.BlockUIBOScreenMixin");
			validMixins.add("compat.BlockUIPaneMixin");
			validMixins.add("compat.BlockUIViewMixin");
			validMixins.add("compat.BlockUIScrollingContainerMixin");
			validMixins.add("compat.BlockUITextFieldMixin");
		}
		
		if(Common.hasMod("supermartijn642corelib")) {
			validMixins.add("compat.SM642WidgetMixin");
			validMixins.add("compat.SM642TextFieldMixin");
		}
		
		if(Common.hasMod("journeymap")) {
			validMixins.add("compat.JourneyMapChatMixin");
		}
	}
}
