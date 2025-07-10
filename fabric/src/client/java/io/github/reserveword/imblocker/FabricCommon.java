package io.github.reserveword.imblocker;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class FabricCommon {
	public static final List<String> defaultScreenWhitelist;

	static {
		defaultScreenWhitelist = Lists.newArrayList(
				"net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen");
		
		if(!IMBlockerCore.isGameVersionReached(771/*1.21.6*/)) {
			defaultScreenWhitelist.add(BookEditScreen.class.getName());
		}
		
		if (IMBlockerCore.isGameVersionReached(761/*1.19.3*/)) {
			defaultScreenWhitelist.add(AbstractSignEditScreen.class.getName());
		} else {
			defaultScreenWhitelist.add(SignEditScreen.class.getName());
		}
	}
}
