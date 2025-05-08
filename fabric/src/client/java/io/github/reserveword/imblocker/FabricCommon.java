package io.github.reserveword.imblocker;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class FabricCommon {
    public static final List<String> defaultScreenWhitelist;
    
    static {
    	defaultScreenWhitelist = Lists.newArrayList(
                BookEditScreen.class.getName(),
                SignEditScreen.class.getName(),
                "journeymap.client.ui.waypoint.WaypointEditor",
                "com.ldtteam.blockout.BOScreen");
    	
    	if(IMBlockerFabric.isGameVersionReached(761/*1.19.3*/)) {
    		defaultScreenWhitelist.add(HangingSignEditScreen.class.getName());
    	}
    }
}
