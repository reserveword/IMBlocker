package io.github.reserveword.imblocker;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.reserveword.imblocker.common.Common;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;

public class ForgeCommon {
	public static final List<String> defaultScreenWhitelist;
    
    static {
    	defaultScreenWhitelist = Lists.newArrayList(
                BookEditScreen.class.getName(),
                "net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen");
    	if(Common.isGameVersionReached(761/*1.19.3*/)) {
    		defaultScreenWhitelist.add(AbstractSignEditScreen.class.getName());
    	}else {
    		defaultScreenWhitelist.add(SignEditScreen.class.getName());
    	}
    }
}
