package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.mixin.forge.ChatScreenMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;

public class ForgeScreenInfo implements IMCheckState.ScreenInfo {
    private final Screen screen;
    public ForgeScreenInfo() {
        screen = Minecraft.getInstance().screen;
    }

    @Override
    public Object get() {
        return screen;
    }

    @Override
    public boolean isChatScreen() {
        return screen instanceof ChatScreen;
    }

    @Override
    public Class<?> type() {
        return screen == null ? null : screen.getClass();
    }

    @Override
    public String defaultText() throws Throwable {
        return ((ChatScreenMixin) screen).getOriginalText();
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        screen.charTyped(codePoint, modifiers);
    }
}
