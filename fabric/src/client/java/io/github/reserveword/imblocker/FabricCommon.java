package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

import java.util.Arrays;
import java.util.List;

public class FabricCommon {
    public static final List<String> defaultScreenWhitelist = Arrays.asList(
            BookEditScreen.class.getName(),
            SignEditScreen.class.getName(),
            HangingSignEditScreen.class.getName(),
            "journeymap.client.ui.waypoint.WaypointEditor",
            "com.ldtteam.blockout.BOScreen");
}
