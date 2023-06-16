package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.mixin.ChatScreenMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class FabricScreenInfo implements IMCheckState.ScreenInfo {
    private final Screen screen;
    public FabricScreenInfo() {
        screen = MinecraftClient.getInstance().currentScreen;;
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
        return screen.getClass();
    }

    @Override
    public String defaultText() throws Throwable {
        return ((ChatScreenMixin) screen).getOriginalText();
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        screen.charTyped(codePoint, modifiers);
    }

    private static final MethodHandle getDefaultInputFieldText;
    static {
        MethodHandle getDefaultInputFieldText_tmp;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            //noinspection JavaLangInvokeHandleSignature
            getDefaultInputFieldText_tmp = lookup.findVirtual(ChatScreen.class, "getDefaultInputFieldText", MethodType.methodType(String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            getDefaultInputFieldText_tmp = null;
            Common.LOGGER.warn("ChatScreen command hook failed:", e);
        }
        getDefaultInputFieldText = getDefaultInputFieldText_tmp;
    }
}
