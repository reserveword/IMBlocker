package io.github.reserveword.imblocker;

import java.util.ArrayList;
import java.util.List;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;

public class ForgeCommon {
	public static final List<String> defaultScreenWhitelist;

	static {
		defaultScreenWhitelist = new ArrayList<>();

		if(!IMBlockerCore.isGameVersionReached(771/*1.21.6*/)) {
			defaultScreenWhitelist.add(BookEditScreen.class.getName());
		}

		defaultScreenWhitelist.add(AbstractSignEditScreen.class.getName());
	}
}
