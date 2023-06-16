package io.github.reserveword.imblocker.asmdummy;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@SuppressWarnings("ALL")
public class FakeChatScreen extends Screen {
    protected FakeChatScreen(Component titleIn) {
        super(titleIn);
    }
    private String defaultInputFieldText = ""; // f_95576_

    public String getDefaultInputFieldText() {
        return defaultInputFieldText;
    }
}
