package io.github.reserveword.imblocker;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;

public class ForgeCommon {
	public static final List<String> defaultScreenWhitelist;

	static {
		defaultScreenWhitelist = Lists.newArrayList();
		
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
