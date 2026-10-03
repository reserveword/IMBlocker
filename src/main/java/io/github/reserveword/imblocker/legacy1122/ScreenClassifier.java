package io.github.reserveword.imblocker.legacy1122;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiWorldEdit;
import net.minecraft.client.gui.inventory.GuiContainerCreative;

final class ScreenClassifier {
    private ScreenClassifier() {}

    static boolean isSpecialInputScreen(GuiScreen screen) {
        if (screen == null) {
            return false;
        }
        if (screen instanceof GuiEditSign || screen instanceof GuiScreenBook
                || screen instanceof GuiCommandBlock) {
            return true;
        }
        String className = screen.getClass().getName();
        for (String allowed : LegacyConfig.screenWhitelist) {
            if (className.equals(allowed)) {
                return true;
            }
        }
        return false;
    }
}
