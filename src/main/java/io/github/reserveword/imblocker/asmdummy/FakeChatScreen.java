package io.github.reserveword.imblocker.asmdummy;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

@SuppressWarnings("ALL")
public class FakeChatScreen extends Screen {
    protected FakeChatScreen(ITextComponent titleIn) {
        super(titleIn);
    }
    private String defaultInputFieldText = "";

    public String getDefaultInputFieldText() {
        return defaultInputFieldText;
    }
}
