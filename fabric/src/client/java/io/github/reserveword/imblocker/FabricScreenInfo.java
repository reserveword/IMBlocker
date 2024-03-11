package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.mixin.fabric.ChatScreenMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

public class FabricScreenInfo implements IMCheckState.ScreenInfo {
    private final Screen screen;
    public FabricScreenInfo() {
        screen = MinecraftClient.getInstance().currentScreen;
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
    public String defaultText() {
        return ((ChatScreenMixin) screen).getOriginalText();
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        screen.charTyped(codePoint, modifiers);
    }

}
