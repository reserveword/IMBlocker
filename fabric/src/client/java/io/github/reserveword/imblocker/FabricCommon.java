package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

import java.util.List;

public class FabricCommon {
    public static final List<String> defaultScreenWhitelist = List.of(
            BookEditScreen.class.getName(),
            SignEditScreen.class.getName(),
            TitleScreen.class.getName(),
            "net.minecraft.client.gui.screen.ingame.HangingSignEditScreen",
            "journeymap.client.ui.waypoint.WaypointEditor",
            "com.ldtteam.blockout.BOScreen");
    public static final List<String> defaultScreenBlacklist = List.of(
            "com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap"
    );
}
