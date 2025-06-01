package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor;
import io.github.reserveword.imblocker.common.accessor.ModLoaderAccessor.Mapping;

public class IMBlockerMixinPlugin implements IMixinConfigPlugin {
	
	private static final ModLoaderAccessor modLoaderAccessor;
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
	
	private static boolean hasMod(String modid) {
		return modLoaderAccessor.hasMod(modid);
	}
	
	private static boolean isGameVersionReached(int protocolVersion) {
		return modLoaderAccessor.isGameVersionReached(protocolVersion);
	}
	
	private static boolean isOfficialMapping() {
		return modLoaderAccessor.getMapping() == Mapping.OFFICIAL;
	}
	
	static {
		Class<?> modEntryClass = null;
		try {
			modEntryClass = Class.forName("io.github.reserveword.imblocker.IMBlocker");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		modLoaderAccessor = ReflectionUtil.invokeMethod(modEntryClass, null, 
				ModLoaderAccessor.class, "getModLoaderAccessor", new Class[0]);
		
		validMixins.add("AbstractCommandBlockScreenMixin");
		validMixins.add("ChatScreenMixin");
		validMixins.add(isOfficialMapping() ? "AbstractWidgetMixin" : "ClickableWidgetMixin");
		validMixins.add("MinecraftClientMixin");
		validMixins.add("WindowMixin");
		
		if(isGameVersionReached(762/*1.19.4*/)) {
			validMixins.add(isOfficialMapping() ? "AbstractContainerEventHandlerMixin" : "AbstractParentElementMixin");
			validMixins.add("TextFieldMixin");
		}else {
			validMixins.add("TextFieldLegacyMixin");
		}
		
		if(isGameVersionReached(760/*1.19.1*/)) {
			if(isOfficialMapping()) {
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
		
		if(hasMod("axiom")) {
			validMixins.add("AxiomEditorUIMixin");
		}
		
		if(hasMod("ftblibrary")) {
			validMixins.add("FtbWidgetMixin");
			
			if(isGameVersionReached(763/*1.20*/)) {
				validMixins.add("FtbTextFieldMixin");
			}else {
				validMixins.add("FtbTextFieldLegacyMixin");
			}
			
			if(isGameVersionReached(760/*1.19.1*/)) {
				validMixins.add("FtbPanelMixin");
				validMixins.add("FtbMultilineTextFieldAccessorImpl");
				validMixins.add("FtbMultilineTextBoxMixin");
			}
		}
		
		if(hasMod("libgui")) {
			validMixins.add("LibGuiWidgetMixin");
			validMixins.add("LibGuiTextFieldMixin");
		}
		
		if(hasMod("emi")) {
			if(isGameVersionReached(762/*1.19.4*/)) {
				validMixins.add("EmiSearchWidgetMixin");
			}else {
				validMixins.add("EmiSearchWidgetLegacyMixin");
			}
		}
		
		if(hasMod("roughlyenoughitems")) {
			validMixins.add("ReiTextFieldMixin");
		}
		
		if(hasMod("replaymod")) {
			validMixins.add("ReplayModTextFieldMixin");
		}
		
		if(hasMod("meteor-client")) {
			validMixins.add("MeteorWidgetMixin");
			validMixins.add("MeteorTextFieldMixin");
		}
		
		if(hasMod("reeses-sodium-options") || hasMod("reeses_sodium_options")) {
			validMixins.add("RSOAbstractFrameMixin");
			validMixins.add("SodiumSearchFieldMixin");
		}
		
		if(hasMod("blockui")) {
			validMixins.add("BlockUIPaneMixin");
			validMixins.add("BlockUIScrollingContainerMixin");
			validMixins.add("BlockUITextFieldMixin");
		}
		
		if(hasMod("supermartijn642corelib")) {
			validMixins.add("SM642WidgetMixin");
			validMixins.add("SM642TextFieldMixin");
		}
	}
}
