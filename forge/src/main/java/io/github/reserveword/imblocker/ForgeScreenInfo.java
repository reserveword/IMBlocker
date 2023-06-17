package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ForgeScreenInfo implements IMCheckState.ScreenInfo {
    private final Screen screen;
    public ForgeScreenInfo() {
        screen = Minecraft.m_91087_().f_91080_;
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
        return (String) getDefaultInputFieldText.invoke(screen);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        screen.m_5534_(codePoint, modifiers);
    }

    private static final MethodHandle getDefaultInputFieldText;
    static {
        MethodHandle getDefaultInputFieldText_tmp;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            //noinspection JavaLangInvokeHandleSignature
            getDefaultInputFieldText_tmp = lookup.findVirtual(ChatScreen.class, "getDefaultInputFieldText", MethodType.methodType(String.class));
        } catch (java.lang.NoSuchMethodException | java.lang.IllegalAccessException e) {
            getDefaultInputFieldText_tmp = null;
            Common.LOGGER.warn("ChatScreen command hook failed:", e);
        }
        getDefaultInputFieldText = getDefaultInputFieldText_tmp;
    }
}
