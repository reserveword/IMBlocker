package io.github.reserveword.imblocker;

import java.util.List;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class FabricCommon {
    public static final List<String> defaultScreenWhitelist = List.of(
            BookEditScreen.class.getName(),
            SignEditScreen.class.getName(),
            HangingSignEditScreen.class.getName(),
            "journeymap.client.ui.waypoint.WaypointEditor",
            "com.ldtteam.blockout.BOScreen");
}
