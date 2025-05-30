package io.github.reserveword.imblocker;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class FabricCommon {
    public static final List<String> defaultScreenWhitelist;
    
    static {
    	defaultScreenWhitelist = Lists.newArrayList(
                BookEditScreen.class.getName(),
                "net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen");
    	
    	if(IMBlocker.getModLoaderAccessor().isGameVersionReached(761/*1.19.3*/)) {
    		defaultScreenWhitelist.add(AbstractSignEditScreen.class.getName());
    	}else {
    		defaultScreenWhitelist.add(SignEditScreen.class.getName());
    	}
    }
}
